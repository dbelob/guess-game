import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { Company } from '../../../shared/models/company/company.model';
import { CubeType } from '../../../shared/models/statistics/olap/cube-type.model';
import { EventType } from '../../../shared/models/event-type/event-type.model';
import { Measure } from '../../../shared/models/statistics/olap/measure.model';
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
import { findEventTypeById, findOrganizerById } from '../../general/utility-functions';

@Component({
  selector: 'app-olap-statistics',
  templateUrl: './olap-statistics.component.html'
})
export class OlapStatisticsComponent implements OnInit {
  private readonly EVENT_TYPES_CUBE_TYPE_KEY = 'cubeType.eventTypes';
  private readonly SPEAKERS_CUBE_TYPE_KEY = 'cubeType.speakers';
  private readonly COMPANIES_CUBE_TYPE_KEY = 'cubeType.companies';

  private readonly DURATION_MEASURE_KEY = 'measure.duration';
  private readonly EVENT_TYPES_QUANTITY_MEASURE_KEY = 'measure.eventTypesQuantity';
  private readonly EVENTS_QUANTITY_MEASURE_KEY = 'measure.eventsQuantity';
  private readonly TALKS_QUANTITY_MEASURE_KEY = 'measure.talksQuantity';
  private readonly SPEAKERS_QUANTITY_MEASURE_KEY = 'measure.speakersQuantity';
  private readonly JAVA_CHAMPIONS_QUANTITY_MEASURE_KEY = 'measure.javaChampionsQuantity';
  private readonly MVPS_QUANTITY_MEASURE_KEY = 'measure.mvpsQuantity';

  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public cubeTypes: CubeType[] = [];
  public selectedCubeType: CubeType;
  public cubeTypeSelectItems: SelectItem[] = [];

  public measures: Measure[] = [];
  public selectedMeasure: Measure;
  public measureSelectItems: SelectItem[] = [];

  public isConferences = true;
  public isMeetups = true;

  public organizers: Organizer[] = [];
  public selectedOrganizer: Organizer;
  public organizerSelectItems: SelectItem[] = [];

  public eventTypes: EventType[] = [];
  public selectedEventType: EventType;
  public eventTypeSelectItems: SelectItem[] = [];

  public selectedSpeakers: Speaker[] = [];
  public speakerSuggestions: Speaker[];

  public selectedCompanies: Company[] = [];
  public companySuggestions: Company[];

  public olapStatistics = new OlapStatistics();

  constructor(private statisticsService: StatisticsService, private eventTypeService: EventTypeService,
              private eventService: EventService, private organizerService: OrganizerService,
              public translateService: TranslateService, private speakerService: SpeakerService,
              private companyService: CompanyService) {
  }

  ngOnInit(): void {
    this.loadCubes();
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

  getMeasureMessageKeyByCube(measure: Measure): string {
    switch (measure) {
      case Measure.Duration: {
        return this.DURATION_MEASURE_KEY;
      }
      case Measure.EventTypesQuantity: {
        return this.EVENT_TYPES_QUANTITY_MEASURE_KEY;
      }
      case Measure.EventsQuantity: {
        return this.EVENTS_QUANTITY_MEASURE_KEY;
      }
      case Measure.TalksQuantity: {
        return this.TALKS_QUANTITY_MEASURE_KEY;
      }
      case Measure.SpeakersQuantity: {
        return this.SPEAKERS_QUANTITY_MEASURE_KEY;
      }
      case Measure.JavaChampionsQuantity: {
        return this.JAVA_CHAMPIONS_QUANTITY_MEASURE_KEY;
      }
      case Measure.MvpsQuantity: {
        return this.MVPS_QUANTITY_MEASURE_KEY;
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

  fillMeasures(measures: Measure[]) {
    this.measures = measures;
    this.measureSelectItems = this.measures.map(m => {
        const messageKey = this.getMeasureMessageKeyByCube(m);

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

  loadCubes() {
    this.statisticsService.getCubeTypes()
      .subscribe(cubeTypeData => {
        this.fillCubeTypes(cubeTypeData);
        this.selectedCubeType = (cubeTypeData && (cubeTypeData.length > 0)) ? cubeTypeData[0] : null;

        this.statisticsService.getMeasures(this.selectedCubeType)
          .subscribe(measureData => {
            this.fillMeasures(measureData);
            this.selectedMeasure = (measureData && (measureData.length > 0)) ? measureData[0] : null;

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
                          this.selectedEventType = (defaultEventData) ? findEventTypeById(defaultEventData.eventTypeId, this.eventTypes) : null;
                        } else {
                          this.selectedEventType = null;
                        }

                        this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasure, this.selectedOrganizer,
                            this.selectedEventType, this.selectedSpeakers, this.selectedCompanies);
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

        this.selectedEventType = null;

        this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasure, this.selectedOrganizer, this.selectedEventType,
            this.selectedSpeakers, this.selectedCompanies);
      });
  }

  loadOlapStatistics(cubeType: CubeType, measure: Measure, organizer: Organizer, eventType: EventType, speakers: Speaker[], companies: Company[]) {
    this.statisticsService.getOlapStatistics(
        new OlapParameters(
            cubeType,
            measure,
            (organizer) ? organizer.id : null,
            (eventType) ? eventType.id : null,
            (speakers) ? speakers.map(s => s.id) : null,
            (companies) ? companies.map(c => c.id) : null))
        .subscribe(data => {
            this.olapStatistics = data;
            console.log('text: ' + data.text)
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
    this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasure, this.selectedOrganizer, this.selectedEventType,
        this.selectedSpeakers, this.selectedCompanies);
  }

  onLanguageChange() {
    const currentSelectedOrganizer = this.selectedOrganizer;
    const currentSelectedEventType = this.selectedEventType;

    this.organizerService.getOrganizers()
      .subscribe(organizerData => {
        this.fillOrganizers(organizerData);

        this.selectedOrganizer = (currentSelectedOrganizer) ? findOrganizerById(currentSelectedOrganizer.id, this.organizers) : null;

        this.eventTypeService.getFilterEventTypes(this.isConferences, this.isMeetups, this.selectedOrganizer)
          .subscribe(eventTypesData => {
            this.fillEventTypes(eventTypesData);

            if (this.eventTypes.length > 0) {
              this.selectedEventType = (currentSelectedEventType) ? findEventTypeById(currentSelectedEventType.id, this.eventTypes) : null;
            } else {
              this.selectedEventType = null;
            }

            this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasure, this.selectedOrganizer, this.selectedEventType,
                this.selectedSpeakers, this.selectedCompanies);
          });
      });
  }

  onCubeChange() {
    this.selectedSpeakers = [];
    this.selectedCompanies = [];

    this.statisticsService.getMeasures(this.selectedCubeType)
      .subscribe(measureData => {
        this.fillMeasures(measureData);
        this.selectedMeasure = (measureData && (measureData.length > 0)) ? measureData[0] : null;

        this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasure, this.selectedOrganizer, this.selectedEventType,
            this.selectedSpeakers, this.selectedCompanies);
      });
  }

  onMeasureChange() {
    this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasure, this.selectedOrganizer, this.selectedEventType,
        this.selectedSpeakers, this.selectedCompanies);
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
    this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasure, this.selectedOrganizer, this.selectedEventType,
        this.selectedSpeakers, this.selectedCompanies);
  }

  unselectSpeaker(event) {
    this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasure, this.selectedOrganizer, this.selectedEventType,
        this.selectedSpeakers, this.selectedCompanies);
  }

  companySearch(event) {
    this.companyService.getCompaniesByFirstLetters(event.query)
      .subscribe(data => {
          this.companySuggestions = data;
        }
      );
  }

  selectCompany(event) {
    this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasure, this.selectedOrganizer, this.selectedEventType,
        this.selectedSpeakers, this.selectedCompanies);
  }

  unselectCompany(event) {
    this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasure, this.selectedOrganizer, this.selectedEventType,
        this.selectedSpeakers, this.selectedCompanies);
  }
}
