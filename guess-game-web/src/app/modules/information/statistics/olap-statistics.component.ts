import { Component, ElementRef, OnInit, QueryList, ViewChildren } from '@angular/core';
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
import { SelectedEntities } from '../../../shared/models/common/selected-entities.model';
import { OlapSpeakerMetrics } from '../../../shared/models/statistics/olap/olap-speaker-metrics.model';
import { OlapCompanyMetrics } from '../../../shared/models/statistics/olap/olap-company-metrics.model';
import { OlapEventTypeParameters } from '../../../shared/models/statistics/olap/olap-event-type-parameters.model';
import { OlapEntityStatistics } from '../../../shared/models/statistics/olap/olap-entity-statistics.model';
import { OlapEventTypeMetrics } from '../../../shared/models/statistics/olap/olap-event-type-metrics.model';
import { OlapSpeakerParameters } from '../../../shared/models/statistics/olap/olap-speaker-parameters.model';
import { OlapEntityMetrics } from '../../../shared/models/statistics/olap/olap-entity-metrics.model';
import { OlapCityParameters } from '../../../shared/models/statistics/olap/olap-city-parameters.model';
import { OlapCityMetrics } from '../../../shared/models/statistics/olap/olap-city-metrics.model';
import { ChartType } from '../../../shared/models/statistics/olap/chart-type.model';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { EventService } from '../../../shared/services/event.service';
import { OrganizerService } from '../../../shared/services/organizer.service';
import { StatisticsService } from '../../../shared/services/statistics.service';
import { SpeakerService } from '../../../shared/services/speaker.service';
import { CompanyService } from '../../../shared/services/company.service';
import {
  findEventTypesByIds,
  findOrganizerById,
  fixOlapEntityStatistics,
  getColorByIndex,
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

  private readonly TOTAL_LABEL_KEY = 'statistics.olap.chart.totalLabel';

  private readonly MEASURE_VALUE_FIELD_NAME_PREFIX = 'measureValue';

  private readonly EVENT_TYPE_CHART_DATASET_QUANTITY = -1;
  private readonly SPEAKER_CHART_DATASET_QUANTITY = 5;
  private readonly COMPANY_CHART_DATASET_QUANTITY = 5;

  private readonly SMALL_WIDTH = 576;
  private readonly MEDIUM_WIDTH = 768;

  private readonly EXTRA_SMALL_ASPECT_RATIO = 1.5;
  private readonly SMALL_ASPECT_RATIO = 2.25;
  private readonly MEDIUM_ASPECT_RATIO = 3;

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
  public cityMultiSortMeta: any[] = [];
  public speakerMultiSortMeta: any[] = [];
  public companyMultiSortMeta: any[] = [];

  public eventTypeExpandedRows: {} = {};
  public speakerExpandedRows: {} = {};
  public companyExpandedRows: {} = {};

  public allLineOptions: any = {};
  public allLineData: any = {};
  public totalLineOptions: any = {};
  public totalLineData: any = {};
  private chartType = ChartType.Details;

  @ViewChildren('chartDiv') chartDivs: QueryList<ElementRef<HTMLDivElement>>;
  private chartDiv: ElementRef<HTMLDivElement>;

  constructor(private statisticsService: StatisticsService, private eventTypeService: EventTypeService,
              private eventService: EventService, private organizerService: OrganizerService,
              public translateService: TranslateService, private speakerService: SpeakerService,
              private companyService: CompanyService) {
    this.eventTypeMultiSortMeta.push({field: 'sortName', order: 1});

    this.cityMultiSortMeta.push({field: 'name', order: 1});

    this.speakerMultiSortMeta.push({field: 'total', order: -1});
    this.speakerMultiSortMeta.push({field: 'name', order: 1});

    this.companyMultiSortMeta.push({field: 'total', order: -1});
    this.companyMultiSortMeta.push({field: 'name', order: 1});
  }

  ngOnInit(): void {
    this.loadCubeTypes();
  }

  ngAfterViewInit(): void {
    window.addEventListener('resize', this.onResize);

    this.chartDivs.changes.subscribe((divs) => {
      this.chartDiv = divs.first;
      this.onResize();
    });
  }

  ngOnDestroy(): void {
    window.removeEventListener('resize', this.onResize);
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

  loadSelectedEntities(complete?: (() => void)) {
    switch (this.selectedCubeType) {
      case CubeType.EventTypes:
        complete();
        break;
      case CubeType.Speakers:
        this.speakerService.getSelectedSpeakers(new SelectedEntities(this.selectedSpeakers.map(s => s.id)))
          .subscribe(speakersData => {
            this.selectedSpeakers = speakersData;
            complete();
          });
        break;
      case CubeType.Companies:
        this.companyService.getSelectedCompanies(new SelectedEntities(this.selectedCompanies.map(c => c.id)))
          .subscribe(companiesData => {
            this.selectedCompanies = companiesData;
            complete();
          });
    }
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
          const olapStatistics = data;

          if (olapStatistics?.eventTypeStatistics) {
            olapStatistics.eventTypeStatistics = getOlapEventTypeStatisticsWithSortName(olapStatistics.eventTypeStatistics);
            fixOlapEntityStatistics(olapStatistics.eventTypeStatistics, this.MEASURE_VALUE_FIELD_NAME_PREFIX)
            this.eventTypeExpandedRows = {};

            this.loadChartTotalData(olapStatistics.eventTypeStatistics);
          }

          if (olapStatistics?.speakerStatistics) {
            fixOlapEntityStatistics(olapStatistics.speakerStatistics, this.MEASURE_VALUE_FIELD_NAME_PREFIX)
            this.speakerExpandedRows = {};

            this.loadChartTotalData(olapStatistics.speakerStatistics);
          }

          if (olapStatistics?.companyStatistics) {
            fixOlapEntityStatistics(olapStatistics.companyStatistics, this.MEASURE_VALUE_FIELD_NAME_PREFIX)
            this.companyExpandedRows = {};

            this.loadChartTotalData(olapStatistics.companyStatistics);
          }

          this.olapStatistics = olapStatistics;
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

            this.loadSelectedEntities(() => {
              this.loadOlapStatistics(this.selectedCubeType, this.selectedMeasureType, this.isConferences, this.isMeetups,
                this.selectedOrganizer, this.selectedEventTypes, this.selectedSpeakers, this.selectedCompanies);
            });
          });
      });
  }

  onCubeTypeChange() {
    this.selectedSpeakers = [];
    this.selectedCompanies = [];
    this.chartType = ChartType.Details;

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
      (this.olapStatistics.eventTypeStatistics.metricsList.length === 0));
  }

  isEventTypesListVisible() {
    return ((this.selectedCubeType === CubeType.EventTypes) &&
      this.olapStatistics?.eventTypeStatistics?.metricsList &&
      (this.olapStatistics.eventTypeStatistics.metricsList.length > 0));
  }

  isNoSpeakersDataFoundVisible() {
    return ((this.selectedCubeType === CubeType.Speakers) &&
      this.olapStatistics?.speakerStatistics?.metricsList &&
      (this.olapStatistics.speakerStatistics.metricsList.length === 0));
  }

  isSpeakersListVisible() {
    return ((this.selectedCubeType === CubeType.Speakers) &&
      this.olapStatistics?.speakerStatistics?.metricsList &&
      (this.olapStatistics.speakerStatistics.metricsList.length > 0));
  }

  isNoCompaniesDataFoundVisible() {
    return ((this.selectedCubeType === CubeType.Companies) &&
      this.olapStatistics?.companyStatistics?.metricsList &&
      (this.olapStatistics.companyStatistics.metricsList.length === 0));
  }

  isCompaniesListVisible() {
    return ((this.selectedCubeType === CubeType.Companies) &&
      this.olapStatistics?.companyStatistics?.metricsList &&
      (this.olapStatistics.companyStatistics.metricsList.length > 0));
  }

  getMeasureValueFieldNamePrefix(num: number): string {
    return this.MEASURE_VALUE_FIELD_NAME_PREFIX + num;
  }

  eventTypeRowExpand(event) {
    const eventTypeMetrics: OlapEventTypeMetrics = event.data;

    if (!eventTypeMetrics.cityStatistics) {
      this.statisticsService.getOlapCityStatistics(
        new OlapCityParameters(
          this.selectedCubeType,
          this.selectedMeasureType,
          eventTypeMetrics.id
        ))
        .subscribe(data => {
            const olapCityStatistics: OlapEntityStatistics<number, OlapCityMetrics> = data;

            fixOlapEntityStatistics(olapCityStatistics, this.MEASURE_VALUE_FIELD_NAME_PREFIX);
            eventTypeMetrics.cityStatistics = olapCityStatistics;
          }
        );
    }
  }

  speakerRowExpand(event) {
    const speakerMetrics: OlapSpeakerMetrics = event.data;

    if (!speakerMetrics.eventTypeStatistics) {
      this.statisticsService.getOlapEventTypeStatistics(
        new OlapEventTypeParameters(
          this.selectedCubeType,
          this.selectedMeasureType,
          this.isConferences,
          this.isMeetups,
          (this.selectedOrganizer) ? this.selectedOrganizer.id : null,
          (this.selectedEventTypes) ? this.selectedEventTypes.map(et => et.id) : null,
          speakerMetrics.id,
          null
        ))
        .subscribe(data => {
            const olapEventTypeStatistics: OlapEntityStatistics<number, OlapEventTypeMetrics> = data;

            fixOlapEntityStatistics(olapEventTypeStatistics, this.MEASURE_VALUE_FIELD_NAME_PREFIX);
            speakerMetrics.eventTypeStatistics = getOlapEventTypeStatisticsWithSortName(olapEventTypeStatistics);
          }
        );
    }
  }

  companyRowExpand(event) {
    const companyMetrics: OlapCompanyMetrics = event.data;

    if (!companyMetrics.eventTypeStatistics) {
      this.statisticsService.getOlapEventTypeStatistics(
        new OlapEventTypeParameters(
          this.selectedCubeType,
          this.selectedMeasureType,
          this.isConferences,
          this.isMeetups,
          (this.selectedOrganizer) ? this.selectedOrganizer.id : null,
          (this.selectedEventTypes) ? this.selectedEventTypes.map(et => et.id) : null,
          null,
          companyMetrics.id
        ))
        .subscribe(data => {
            const olapEventTypeStatistics: OlapEntityStatistics<number, OlapEventTypeMetrics> = data;

            fixOlapEntityStatistics(olapEventTypeStatistics, this.MEASURE_VALUE_FIELD_NAME_PREFIX);
            olapEventTypeStatistics.metricsList.forEach(m => m.companyId = companyMetrics.id);
            companyMetrics.eventTypeStatistics = getOlapEventTypeStatisticsWithSortName(olapEventTypeStatistics);
          }
        );
    }
  }

  companyEventTypeRowExpand(event) {
    const eventTypeMetrics: OlapEventTypeMetrics = event.data;

    if (!eventTypeMetrics.speakerStatistics) {
      this.statisticsService.getOlapSpeakerStatistics(
        new OlapSpeakerParameters(
          this.selectedCubeType,
          this.selectedMeasureType,
          eventTypeMetrics.companyId,
          eventTypeMetrics.id
        ))
        .subscribe(data => {
            const olapSpeakerStatistics: OlapEntityStatistics<number, OlapSpeakerMetrics> = data;

            fixOlapEntityStatistics(olapSpeakerStatistics, this.MEASURE_VALUE_FIELD_NAME_PREFIX);
            eventTypeMetrics.speakerStatistics = olapSpeakerStatistics;
          }
        );
    }
  }

  createLineOptions(aspectRatio: number): any {
    return {
      animation: false,
      aspectRatio: aspectRatio
    };
  }

  loadChartDetailsData(olapEntityStatistics: OlapEntityStatistics<number, OlapEntityMetrics>,
                       sortedMetricsList: OlapEntityMetrics[], quantity: number) {
    const metricsList = (quantity <= 0) ? sortedMetricsList : sortedMetricsList.slice(0, quantity);

    this.allLineData = {
      labels: olapEntityStatistics.dimensionValues,
      datasets: metricsList.map((value, index) => {
        const color = getColorByIndex(index);

        return {
          label: value.name,
          data: value.measureValues,
          fill: false,
          tension: 0.4,
          backgroundColor: color,
          borderColor: color
        }
      })
    };
  }

  loadChartTotalData(olapEntityStatistics: OlapEntityStatistics<number, OlapEntityMetrics>) {
    const color = getColorByIndex(0);

    this.translateService.get(this.TOTAL_LABEL_KEY)
      .subscribe(data => {
          this.totalLineData = {
            labels: olapEntityStatistics.dimensionValues,
            datasets: [
              {
                label: data,
                data: olapEntityStatistics.totals.measureValues,
                fill: false,
                tension: 0.4,
                backgroundColor: color,
                borderColor: color
              }
            ]
          };
        }
      );
  }

  getChartType(): string {
    switch (this.chartType) {
      case ChartType.Details:
        return 'details';
      case ChartType.Total:
        return 'total';
      default:
        return null;
    }
  }

  onResize = (): void => {
    if (this.chartDiv) {
      const clientWidth = this.chartDiv.nativeElement.clientWidth;
      const aspectRatio = (clientWidth < this.SMALL_WIDTH) ? this.EXTRA_SMALL_ASPECT_RATIO :
        ((clientWidth < this.MEDIUM_WIDTH) ? this.SMALL_ASPECT_RATIO : this.MEDIUM_ASPECT_RATIO);

      this.allLineOptions = this.createLineOptions(aspectRatio);
      this.totalLineOptions = this.createLineOptions(aspectRatio);
    }
  }

  isChartsVisible() {
    return this.isEventTypesListVisible() || this.isSpeakersListVisible() || this.isCompaniesListVisible();
  }

  isDetailsChartVisible() {
    return (this.chartType === ChartType.Details);
  }

  isTotalChartVisible() {
    return (this.chartType === ChartType.Total);
  }

  detailsChart() {
    this.chartType = ChartType.Details;
  }

  totalChart() {
    this.chartType = ChartType.Total;
  }

  sortEventTypeStatistics(value) {
    this.loadChartDetailsData(this.olapStatistics.eventTypeStatistics, value, this.EVENT_TYPE_CHART_DATASET_QUANTITY);
  }

  sortSpeakerStatistics(value) {
    this.loadChartDetailsData(this.olapStatistics.speakerStatistics, value, this.SPEAKER_CHART_DATASET_QUANTITY);
  }

  sortCompanyStatistics(value) {
    this.loadChartDetailsData(this.olapStatistics.companyStatistics, value, this.COMPANY_CHART_DATASET_QUANTITY);
  }
}
