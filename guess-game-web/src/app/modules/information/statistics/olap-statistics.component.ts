import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { Cube } from '../../../shared/models/statistics/olap/cube.model';
import { EventType } from '../../../shared/models/event-type/event-type.model';
import { Measure } from '../../../shared/models/statistics/olap/measure.model';
import { Organizer } from '../../../shared/models/organizer/organizer.model';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { EventService } from '../../../shared/services/event.service';
import { OrganizerService } from '../../../shared/services/organizer.service';
import { StatisticsService } from '../../../shared/services/statistics.service';
import { findEventTypeById, findOrganizerById } from '../../general/utility-functions';

@Component({
  selector: 'app-olap-statistics',
  templateUrl: './olap-statistics.component.html'
})
export class OlapStatisticsComponent implements OnInit {
  private readonly EVENT_TYPES_CUBE_KEY = 'cube.eventTypes';
  private readonly SPEAKERS_CUBE_KEY = 'cube.speakers';
  private readonly COMPANIES_CUBE_KEY = 'cube.companies';

  private readonly DURATION_MEASURE_KEY = 'measure.duration';
  private readonly EVENT_TYPES_QUANTITY_MEASURE_KEY = 'measure.eventTypesQuantity';
  private readonly EVENTS_QUANTITY_MEASURE_KEY = 'measure.eventsQuantity';
  private readonly TALKS_QUANTITY_MEASURE_KEY = 'measure.talksQuantity';
  private readonly SPEAKERS_QUANTITY_MEASURE_KEY = 'measure.speakersQuantity';
  private readonly JAVA_CHAMPIONS_QUANTITY_MEASURE_KEY = 'measure.javaChampionsQuantity';
  private readonly MVPS_QUANTITY_MEASURE_KEY = 'measure.mvpsQuantity';

  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public cubes: Cube[] = [];
  public selectedCube: Cube;
  public cubeSelectItems: SelectItem[] = [];

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

  constructor(private statisticsService: StatisticsService, private eventTypeService: EventTypeService,
              private eventService: EventService, public organizerService: OrganizerService,
              public translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.loadCubes();
    this.loadOrganizers();
  }

  getCubeMessageKeyByCube(cube: Cube): string {
    switch (cube) {
      case Cube.EventTypes: {
        return this.EVENT_TYPES_CUBE_KEY;
      }
      case Cube.Speakers: {
        return this.SPEAKERS_CUBE_KEY;
      }
      case Cube.Companies: {
        return this.COMPANIES_CUBE_KEY;
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

  fillCubes(cubes: Cube[]) {
    this.cubes = cubes;
    this.cubeSelectItems = this.cubes.map(c => {
        const messageKey = this.getCubeMessageKeyByCube(c);

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
    this.statisticsService.getCubes()
      .subscribe(cubeData => {
        this.fillCubes(cubeData);
        this.selectedCube = (cubeData && (cubeData.length > 0)) ? cubeData[0] : null;

        this.statisticsService.getMeasures(this.selectedCube)
          .subscribe(measureData => {
            this.fillMeasures(measureData);
            this.selectedMeasure = (measureData && (measureData.length > 0)) ? measureData[0] : null;

            // TODO: implement
          });

        // TODO: implement
      });
  }

  loadOrganizers() {
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

                this.loadOlapStatistics(this.selectedOrganizer, this.selectedEventType);
              });
          });
      });
  }

  loadEventTypes() {
    this.eventTypeService.getFilterEventTypes(this.isConferences, this.isMeetups, this.selectedOrganizer)
      .subscribe(eventTypesData => {
        this.fillEventTypes(eventTypesData);

        this.selectedEventType = null;

        this.loadOlapStatistics(this.selectedOrganizer, this.selectedEventType);
      });
  }

  loadOlapStatistics(organizer: Organizer, eventType: EventType) {
    // TODO: implement
    console.log('Load OLAP statistics; organizer id: ' +
      ((organizer) ? organizer.id : null) +
      ', eventType id: ' +
      ((eventType) ? eventType.id : null));
  }

  onEventTypeKindChange() {
    this.loadEventTypes();
  }

  onOrganizerChange() {
    this.loadEventTypes();
  }

  onEventTypeChange() {
    this.loadOlapStatistics(this.selectedOrganizer, this.selectedEventType);
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

            this.loadOlapStatistics(this.selectedOrganizer, this.selectedEventType);
          });
      });
  }

  onCubeChange() {
    // TODO: implement
  }

  onMeasureChange() {
    // TODO: implement
  }
}
