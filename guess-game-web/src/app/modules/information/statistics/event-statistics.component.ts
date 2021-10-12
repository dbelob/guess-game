import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type/event-type.model';
import { EventStatistics } from '../../../shared/models/statistics/event-statistics.model';
import { Organizer } from '../../../shared/models/organizer/organizer.model';
import { StatisticsService } from '../../../shared/services/statistics.service';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { EventService } from '../../../shared/services/event.service';
import { OrganizerService } from '../../../shared/services/organizer.service';
import { findEventTypeById, findOrganizerById } from '../../general/utility-functions';

@Component({
  selector: 'app-event-statistics',
  templateUrl: './event-statistics.component.html'
})
export class EventStatisticsComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public organizers: Organizer[] = [];
  public selectedOrganizer: Organizer;
  public organizerSelectItems: SelectItem[] = [];

  public conferences: EventType[] = [];
  public selectedConference: EventType;
  public conferenceSelectItems: SelectItem[] = [];

  public eventStatistics = new EventStatistics();
  public multiSortMeta: any[] = [];

  constructor(private statisticsService: StatisticsService, private eventTypeService: EventTypeService,
              private eventService: EventService, public organizerService: OrganizerService,
              public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'name', order: 1});
  }

  ngOnInit(): void {
    this.loadOrganizers();
  }

  fillOrganizers(organizers: Organizer[]) {
    this.organizers = organizers;
    this.organizerSelectItems = this.organizers.map(o => {
        return {label: o.name, value: o};
      }
    );
  }

  fillConferences(conferences: EventType[]) {
    this.conferences = conferences;
    this.conferenceSelectItems = this.conferences.map(et => {
        return {label: et.name, value: et};
      }
    );
  }

  loadOrganizers() {
    this.organizerService.getOrganizers()
      .subscribe(organizerData => {
        this.fillOrganizers(organizerData);

        this.eventService.getDefaultEvent()
          .subscribe(defaultEventData => {
            this.selectedOrganizer = (defaultEventData) ? findOrganizerById(defaultEventData.organizerId, this.organizers) : null;

            this.eventTypeService.getFilterConferences(this.selectedOrganizer)
              .subscribe(eventTypesData => {
                this.fillConferences(eventTypesData);

                if (this.conferences.length > 0) {
                  this.selectedConference = (defaultEventData) ? findEventTypeById(defaultEventData.eventTypeId, this.conferences) : null;
                } else {
                  this.selectedConference = null;
                }

                this.loadEventStatistics(this.selectedOrganizer, this.selectedConference);
              });
          });
      });
  }

  loadConferences() {
    this.eventTypeService.getFilterConferences(this.selectedOrganizer)
      .subscribe(eventTypesData => {
        this.fillConferences(eventTypesData);

        this.selectedConference = null;

        this.loadEventStatistics(this.selectedOrganizer, this.selectedConference);
      });
  }

  loadEventStatistics(organizer: Organizer, conference: EventType) {
    this.statisticsService.getEventStatistics(organizer, conference)
      .subscribe(data => {
          this.eventStatistics = data;
        }
      );
  }

  onOrganizerChange() {
    this.loadConferences();
  }

  onConferenceChange(conference: EventType) {
    this.loadEventStatistics(this.selectedOrganizer, conference);
  }

  onLanguageChange() {
    const currentSelectedOrganizer = this.selectedOrganizer;
    const currentSelectedConference = this.selectedConference;

    this.organizerService.getOrganizers()
      .subscribe(organizerData => {
        this.fillOrganizers(organizerData);

        this.selectedOrganizer = (currentSelectedOrganizer) ? findOrganizerById(currentSelectedOrganizer.id, this.organizers) : null;

        this.eventTypeService.getFilterConferences(this.selectedOrganizer)
          .subscribe(eventTypesData => {
            this.fillConferences(eventTypesData);

            if (this.conferences.length > 0) {
              this.selectedConference = (currentSelectedConference) ? findEventTypeById(currentSelectedConference.id, this.conferences) : null;
            } else {
              this.selectedConference = null;
            }

            this.loadEventStatistics(this.selectedOrganizer, this.selectedConference);
          });
      });
  }

  isNoEventsFoundVisible() {
    return (this.eventStatistics?.eventMetricsList && (this.eventStatistics.eventMetricsList.length === 0));
  }

  isEventsListVisible() {
    return (this.eventStatistics?.eventMetricsList && (this.eventStatistics.eventMetricsList.length > 0));
  }
}
