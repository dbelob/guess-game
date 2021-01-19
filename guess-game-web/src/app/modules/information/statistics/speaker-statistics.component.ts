import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { EventType } from '../../../shared/models/event-type/event-type.model';
import { SpeakerStatistics } from '../../../shared/models/statistics/speaker-statistics.model';
import { Organizer } from '../../../shared/models/organizer/organizer.model';
import { StatisticsService } from '../../../shared/services/statistics.service';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { EventService } from '../../../shared/services/event.service';
import { OrganizerService } from '../../../shared/services/organizer.service';
import { findEventTypeById, findOrganizerById } from '../../general/utility-functions';

@Component({
  selector: 'app-speaker-statistics',
  templateUrl: './speaker-statistics.component.html'
})
export class SpeakerStatisticsComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;
  public degreesImageDirectory = `${this.imageDirectory}/degrees`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;

  public isConferences = true;
  public isMeetups = true;

  public organizers: Organizer[] = [];
  public selectedOrganizer: Organizer;
  public organizerSelectItems: SelectItem[] = [];

  public eventTypes: EventType[] = [];
  public selectedEventType: EventType;
  public eventTypeSelectItems: SelectItem[] = [];

  public speakerStatistics = new SpeakerStatistics();
  public multiSortMeta: any[] = [];

  constructor(private statisticsService: StatisticsService, private eventTypeService: EventTypeService,
              private eventService: EventService, public organizerService: OrganizerService,
              public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'talksQuantity', order: -1});
    this.multiSortMeta.push({field: 'eventsQuantity', order: -1});
    this.multiSortMeta.push({field: 'eventTypesQuantity', order: -1});
  }

  ngOnInit(): void {
    this.loadEventTypes();
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

  loadEventTypes() {
    this.organizerService.getOrganizers()
      .subscribe(organizerData => {
        this.fillOrganizers(organizerData);

        this.eventService.getDefaultEvent()
          .subscribe(defaultEventData => {
            const defaultOrganizerId = defaultEventData?.organizerId;
            const selectedOrganizer = (defaultOrganizerId) ? findOrganizerById(defaultOrganizerId, this.organizers) : null;
            this.selectedOrganizer = (selectedOrganizer) ? selectedOrganizer : null;

            this.eventTypeService.getFilterEventTypes(this.isConferences, this.isMeetups)
              .subscribe(eventTypesData => {
                this.fillEventTypes(eventTypesData);

                if (this.eventTypes.length > 0) {
                  const selectedEventType = (defaultEventData) ? findEventTypeById(defaultEventData.eventTypeId, this.eventTypes) : null;
                  this.selectedEventType = (selectedEventType) ? selectedEventType : null;
                } else {
                  this.selectedEventType = null;
                }

                this.loadSpeakerStatistics(this.selectedEventType);
              });
          });
      });
  }

  loadSpeakerStatistics(eventType: EventType) {
    this.statisticsService.getSpeakerStatistics(this.isConferences, this.isMeetups, eventType)
      .subscribe(data => {
          this.speakerStatistics = data;
        }
      );
  }

  onEventTypeChange(eventType: EventType) {
    this.loadSpeakerStatistics(eventType);
  }

  onEventTypeKindChange(checked: boolean) {
    this.loadEventTypes();
  }

  onOrganizerChange(organizer: Organizer) {
    // TODO: implement
  }

  onLanguageChange() {
    const currentSelectedOrganizer = this.selectedOrganizer;
    const currentSelectedEventType = this.selectedEventType;

    this.organizerService.getOrganizers()
      .subscribe(organizerData => {
        this.fillOrganizers(organizerData);

        const defaultOrganizerId = currentSelectedOrganizer?.id;
        const selectedOrganizer = (defaultOrganizerId) ? findOrganizerById(defaultOrganizerId, this.organizers) : null;
        this.selectedOrganizer = (selectedOrganizer) ? selectedOrganizer : null;

        this.eventTypeService.getFilterEventTypes(this.isConferences, this.isMeetups)
          .subscribe(eventTypesData => {
            this.fillEventTypes(eventTypesData);

            if (this.eventTypes.length > 0) {
              this.selectedEventType = (currentSelectedEventType) ? findEventTypeById(currentSelectedEventType.id, this.eventTypes) : null;
            } else {
              this.selectedEventType = null;
            }

            this.loadSpeakerStatistics(this.selectedEventType);
          });
      });
  }

  isNoSpeakersFoundVisible() {
    return (this.speakerStatistics?.speakerMetricsList && (this.speakerStatistics.speakerMetricsList.length === 0));
  }

  isSpeakersListVisible() {
    return (this.speakerStatistics?.speakerMetricsList && (this.speakerStatistics.speakerMetricsList.length > 0));
  }
}
