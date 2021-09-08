import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { Company } from '../../../shared/models/company/company.model';
import { CubeType } from '../../../shared/models/statistics/olap/cube-type.model';
import { EventType } from '../../../shared/models/event-type/event-type.model';
import { MeasureType } from '../../../shared/models/statistics/olap/measure-type.model';
import { OlapParameters } from '../../../shared/models/statistics/olap/olap.parameters.model';
import { OlapStatistics } from '../../../shared/models/statistics/olap/olap-statistics.model';
import { Organizer } from '../../../shared/models/organizer/organizer.model';
import { Speaker } from '../../../shared/models/speaker/speaker.model';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { EventService } from '../../../shared/services/event.service';
import { OrganizerService } from '../../../shared/services/organizer.service';
import { StatisticsService } from '../../../shared/services/statistics.service';
import { SpeakerService } from '../../../shared/services/speaker.service';
import { CompanyService } from '../../../shared/services/company.service';
import {
    findEventTypesByIds,
    findOrganizerById,
    getOlapEventTypeStatisticsWithSortName
} from '../../general/utility-functions';

@Component({
  selector: 'app-olap-statistics',
  templateUrl: './olap-statistics.component.html'
})
export class OlapStatisticsComponent implements OnInit {
  private readonly EVENT_TYPES_CUBE_TYPE_KEY = 'cubeType.eventTypes';
  private readonly SPEAKERS_CUBE_TYPE_KEY = 'cubeType.speakers';
  private readonly COMPANIES_CUBE_TYPE_KEY = 'cubeType.companies';

  private readonly DURATION_MEASURE_TYPE_KEY = 'measureType.duration';
  private readonly EVENT_TYPES_QUANTITY_MEASURE_TYPE_KEY = 'measureType.eventTypesQuantity';
  private readonly EVENTS_QUANTITY_MEASURE_TYPE_KEY = 'measureType.eventsQuantity';
  private readonly TALKS_QUANTITY_MEASURE_TYPE_KEY = 'measureType.talksQuantity';
  private readonly SPEAKERS_QUANTITY_MEASURE_TYPE_KEY = 'measureType.speakersQuantity';
  private readonly JAVA_CHAMPIONS_QUANTITY_MEASURE_TYPE_KEY = 'measureType.javaChampionsQuantity';
  private readonly MVPS_QUANTITY_MEASURE_TYPE_KEY = 'measureType.mvpsQuantity';

  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;

  public cubeTypes: CubeType[] = [];
  public selectedCubeType: CubeType;
  public cubeTypeSelectItems: SelectItem[] = [];

  public measureTypes: MeasureType[] = [];
  public selectedMeasureType: MeasureType;
  public measureTypeSelectItems: SelectItem[] = [];

  public isConferences = true;
  public isMeetups = true;

  public organizers: Organizer[] = [];
  public selectedOrganizer: Organizer;
  public organizerSelectItems: SelectItem[] = [];

  public eventTypes: EventType[] = [];
  public selectedEventTypes: EventType[] = [];
  public eventTypeSelectItems: SelectItem[] = [];

  public selectedSpeakers: Speaker[] = [];
  public speakerSuggestions: Speaker[];

  public selectedCompanies: Company[] = [];
  public companySuggestions: Company[];

  public olapStatistics = new OlapStatistics();
  public eventTypeMultiSortMeta: any[] = [];
  public speakerMultiSortMeta: any[] = [];
  public companyMultiSortMeta: any[] = [];

  constructor(private statisticsService: StatisticsService, private eventTypeService: EventTypeService,
              private eventService: EventService, private organizerService: OrganizerService,
              public translateService: TranslateService, private speakerService: SpeakerService,
              private companyService: CompanyService) {
    this.eventTypeMultiSortMeta.push({field: 'sortName', order: 1});
    this.speakerMultiSortMeta.push({field: 'name', order: 1});
    this.companyMultiSortMeta.push({field: 'name', order: 1});
  }

  ngOnInit(): void {
    this.loadCubeTypes();
  }

  getCubeTypeMessageKeyByCube(cubeType: CubeType): string {
    switch (cubeType) {
      case CubeType.EventTypes: {
        return this.EVENT_TYPES_CUBE_TYPE_KEY;
      }
      case CubeType.Speakers: {
        return this.SPEAKERS_CUBE_TYPE_KEY;
      }
      case CubeType.Companies: {
        return this.COMPANIES_CUBE_TYPE_KEY;
      }
      default: {
        return null;
      }
    }
  }

  getMeasureTypeMessageKeyByCube(measureType: MeasureType): string {
    switch (measureType) {
      case MeasureType.Duration: {
        return this.DURATION_MEASURE_TYPE_KEY;
      }
      case MeasureType.EventTypesQuantity: {
        return this.EVENT_TYPES_QUANTITY_MEASURE_TYPE_KEY;
      }
      case MeasureType.EventsQuantity: {
        return this.EVENTS_QUANTITY_MEASURE_TYPE_KEY;
      }
      case MeasureType.TalksQuantity: {
        return this.TALKS_QUANTITY_MEASURE_TYPE_KEY;
      }
      case MeasureType.SpeakersQuantity: {
        return this.SPEAKERS_QUANTITY_MEASURE_TYPE_KEY;
      }
      case MeasureType.JavaChampionsQuantity: {
        return this.JAVA_CHAMPIONS_QUANTITY_MEASURE_TYPE_KEY;
      }
      case MeasureType.MvpsQuantity: {
        return this.MVPS_QUANTITY_MEASURE_TYPE_KEY;
      }
      default: {
        return null;
      }
    }
  }

  fillCubeTypes(cubeTypes: CubeType[]) {
    this.cubeTypes = cubeTypes;
    this.cubeTypeSelectItems = this.cubeTypes.map(c => {
        const messageKey = this.getCubeTypeMessageKeyByCube(c);

        return {label: messageKey, value: c};
      }
    );
  }

  fillMeasureTypes(measureTypes: MeasureType[]) {
    this.measureTypes = measureTypes;
    this.measureTypeSelectItems = this.measureTypes.map(m => {
        const messageKey = this.getMeasureTypeMessageKeyByCube(m);

        return {label: messageKey, value: m};
      }
    );
  }

  fillOrganizers(organizers: Organizer[]) {
    this.organizers = organizers;
    this.organizerSelectItems = this.organizers.map(o => {
        return {label: o.name, value: o};
      }
    );
  }

  fillEventTypes(eventTypes: EventType[]) {
    this.eventTypes = eventTypes;
    this.eventTypeSelectItems = this.eventTypes.map(et => {
        return {label: et.name, value: et};
      }
    );
  }

  loadCubeTypes() {
    this.statisticsService.getCubeTypes()
      .subscribe(cubeTypeData => {
        this.fillCubeTypes(cubeTypeData);
        this.selectedCubeType = (cubeTypeData && (cubeTypeData.length > 0)) ? cubeTypeData[0] : null;

        this.statisticsService.getMeasureTypes(this.selectedCubeType)
          .subscribe(measureTypeData => {
            this.fillMeasureTypes(measureTypeData);
            this.selectedMeasureType = (measureTypeData && (measureTypeData.length > 0)) ? measureTypeData[0] : null;

            this.organizerService.getOrganizers()
              .subscribe(organizerData => {
                this.fillOrganizers(organizerData);

                this.eventService.getDefaultEvent()
                  .subscribe(defaultEventData => {
                    this.selectedOrganizer = (defaultEventData) ? findOrganizerById(defaultEventData.organizerId, this.organizers) : null;

                    this.eventTypeService.getFilterEventTypes(this.isConferences, this.isMeetups, this.selectedOrganizer)
                      .subscribe(eventTypesData => {
                        this.fillEventTypes(eventTypesData);

                        if (this.eventTypes.length > 0) {
                          this.selectedEventTypes = (defaultEventData) ? findEventTypesByIds([defaultEventData.eventTypeId], this.eventTypes) : [];
                        } else {
                          this.selectedEventTypes = [];
                        }

                        this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasureType, this.isConferences, this.isMeetups,
                          this.selectedOrganizer, this.selectedEventTypes, this.selectedSpeakers, this.selectedCompanies);
                      });
                  });
              });
          });
      });
  }

  loadEventTypes() {
    this.eventTypeService.getFilterEventTypes(this.isConferences, this.isMeetups, this.selectedOrganizer)
      .subscribe(eventTypesData => {
        this.fillEventTypes(eventTypesData);

        this.selectedEventTypes = [];

        this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasureType, this.isConferences, this.isMeetups,
          this.selectedOrganizer, this.selectedEventTypes, this.selectedSpeakers, this.selectedCompanies);
      });
  }

  loadOlapStatistics(cubeType: CubeType, measureType: MeasureType, isConferences: boolean, isMeetups: boolean,
                     organizer: Organizer, eventTypes: EventType[], speakers: Speaker[], companies: Company[]) {
    this.statisticsService.getOlapStatistics(
      new OlapParameters(
        cubeType,
        measureType,
        isConferences,
        isMeetups,
        (organizer) ? organizer.id : null,
        (eventTypes) ? eventTypes.map(et => et.id) : null,
        (speakers) ? speakers.map(s => s.id) : null,
        (companies) ? companies.map(c => c.id) : null))
      .subscribe(data => {
          this.olapStatistics = data;

          if (this.olapStatistics?.eventTypeStatistics) {
            this.olapStatistics.eventTypeStatistics = getOlapEventTypeStatisticsWithSortName(this.olapStatistics.eventTypeStatistics);
          }
        }
      );
  }

  onEventTypeKindChange() {
    this.loadEventTypes();
  }

  onOrganizerChange() {
    this.loadEventTypes();
  }

  onEventTypeChange() {
    this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasureType, this.isConferences, this.isMeetups,
      this.selectedOrganizer, this.selectedEventTypes, this.selectedSpeakers, this.selectedCompanies);
  }

  onLanguageChange() {
    const currentSelectedOrganizer = this.selectedOrganizer;
    const currentSelectedEventTypes = this.selectedEventTypes;

    this.organizerService.getOrganizers()
      .subscribe(organizerData => {
        this.fillOrganizers(organizerData);

        this.selectedOrganizer = (currentSelectedOrganizer) ? findOrganizerById(currentSelectedOrganizer.id, this.organizers) : null;

        this.eventTypeService.getFilterEventTypes(this.isConferences, this.isMeetups, this.selectedOrganizer)
          .subscribe(eventTypesData => {
            this.fillEventTypes(eventTypesData);

            if (this.eventTypes.length > 0) {
              this.selectedEventTypes = (currentSelectedEventTypes) ? findEventTypesByIds(currentSelectedEventTypes.map(et => et.id), this.eventTypes) : [];
            } else {
              this.selectedEventTypes = [];
            }

            this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasureType, this.isConferences, this.isMeetups,
              this.selectedOrganizer, this.selectedEventTypes, this.selectedSpeakers, this.selectedCompanies);
          });
      });
  }

  onCubeTypeChange() {
    this.selectedSpeakers = [];
    this.selectedCompanies = [];

    this.statisticsService.getMeasureTypes(this.selectedCubeType)
      .subscribe(measureTypeData => {
        this.fillMeasureTypes(measureTypeData);
        this.selectedMeasureType = (measureTypeData && (measureTypeData.length > 0)) ? measureTypeData[0] : null;

        this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasureType, this.isConferences, this.isMeetups,
          this.selectedOrganizer, this.selectedEventTypes, this.selectedSpeakers, this.selectedCompanies);
      });
  }

  onMeasureTypeChange() {
    this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasureType, this.isConferences, this.isMeetups,
      this.selectedOrganizer, this.selectedEventTypes, this.selectedSpeakers, this.selectedCompanies);
  }

  isSpeakersVisible(): boolean {
    return (CubeType.Speakers === this.selectedCubeType);
  }

  isCompaniesVisible(): boolean {
    return (CubeType.Companies == this.selectedCubeType);
  }

  speakerSearch(event) {
    this.speakerService.getSpeakersByFirstLetters(event.query)
      .subscribe(data => {
          this.speakerSuggestions = data;
        }
      );
  }

  selectSpeaker(event) {
    this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasureType, this.isConferences, this.isMeetups,
      this.selectedOrganizer, this.selectedEventTypes, this.selectedSpeakers, this.selectedCompanies);
  }

  unselectSpeaker(event) {
    this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasureType, this.isConferences, this.isMeetups,
      this.selectedOrganizer, this.selectedEventTypes, this.selectedSpeakers, this.selectedCompanies);
  }

  companySearch(event) {
    this.companyService.getCompaniesByFirstLetters(event.query)
      .subscribe(data => {
          this.companySuggestions = data;
        }
      );
  }

  selectCompany(event) {
    this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasureType, this.isConferences, this.isMeetups,
      this.selectedOrganizer, this.selectedEventTypes, this.selectedSpeakers, this.selectedCompanies);
  }

  unselectCompany(event) {
    this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasureType, this.isConferences, this.isMeetups,
      this.selectedOrganizer, this.selectedEventTypes, this.selectedSpeakers, this.selectedCompanies);
  }

  isNoEventTypesDataFoundVisible() {
    return ((this.selectedCubeType === CubeType.EventTypes) &&
      this.olapStatistics?.eventTypeStatistics?.metricsList &&
      (this.olapStatistics?.eventTypeStatistics.metricsList.length === 0));
  }

  isEventTypesListVisible() {
    return ((this.selectedCubeType === CubeType.EventTypes) &&
      this.olapStatistics?.eventTypeStatistics?.metricsList &&
      (this.olapStatistics?.eventTypeStatistics.metricsList.length > 0));
  }

  isNoSpeakersDataFoundVisible() {
    return ((this.selectedCubeType === CubeType.Speakers) &&
      this.olapStatistics?.speakerStatistics?.metricsList &&
      (this.olapStatistics?.speakerStatistics.metricsList.length === 0));
  }

  isSpeakersListVisible() {
    return ((this.selectedCubeType === CubeType.Speakers) &&
      this.olapStatistics?.speakerStatistics?.metricsList &&
      (this.olapStatistics?.speakerStatistics.metricsList.length > 0));
  }

  isNoCompaniesDataFoundVisible() {
    return ((this.selectedCubeType === CubeType.Companies) &&
      this.olapStatistics?.companyStatistics?.metricsList &&
      (this.olapStatistics?.companyStatistics.metricsList.length === 0));
  }

  isCompaniesListVisible() {
    return ((this.selectedCubeType === CubeType.Companies) &&
      this.olapStatistics?.companyStatistics?.metricsList &&
      (this.olapStatistics?.companyStatistics.metricsList.length > 0));
  }
}
