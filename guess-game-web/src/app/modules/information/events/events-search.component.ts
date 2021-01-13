import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type/event-type.model';
import { Event } from '../../../shared/models/event/event.model';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { EventService } from '../../../shared/services/event.service';
import { findEventTypeById } from '../../general/utility-functions';

@Component({
  selector: 'app-events-search',
  templateUrl: './events-search.component.html'
})
export class EventsSearchComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public isConferences = true;
  public isMeetups = true;

  public eventTypes: EventType[] = [];
  public selectedEventType: EventType;
  public eventTypeSelectItems: SelectItem[] = [];

  public events: Event[] = [];
  public multiSortMeta: any[] = [];

  constructor(private eventTypeService: EventTypeService, private eventService: EventService,
              public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'startDate', order: -1});
  }

  ngOnInit(): void {
    this.loadEventTypes(this.isConferences, this.isMeetups);
  }

  loadEventTypes(isConferences: boolean, isMeetups: boolean) {
    this.eventTypeService.getFilterEventTypes(isConferences, isMeetups)
      .subscribe(eventTypesData => {
        this.eventTypes = eventTypesData;
        this.eventTypeSelectItems = this.eventTypes.map(et => {
            return {label: et.name, value: et};
          }
        );

        if (this.eventTypes.length > 0) {
          this.eventService.getDefaultEvent()
            .subscribe(defaultEventData => {
              const selectedEventType = (defaultEventData) ? findEventTypeById(defaultEventData.eventTypeId, this.eventTypes) : null;

              if (selectedEventType) {
                this.selectedEventType = selectedEventType;
              } else {
                this.selectedEventType = null;
              }

              this.loadEvents(this.isConferences, this.isMeetups, this.selectedEventType);
            });
        } else {
          this.selectedEventType = null;
          this.loadEvents(this.isConferences, this.isMeetups, this.selectedEventType);
        }
      });
  }

  loadEvents(isConferences: boolean, isMeetups: boolean, eventType: EventType) {
    this.eventService.getEvents(isConferences, isMeetups, eventType)
      .subscribe(data => {
        this.events = data;
      });
  }

  onEventTypeChange(eventType: EventType) {
    this.loadEvents(this.isConferences, this.isMeetups, eventType);
  }

  onEventTypeKindChange() {
    this.loadEventTypes(this.isConferences, this.isMeetups);
  }

  onLanguageChange() {
    this.loadEventTypes(this.isConferences, this.isMeetups);
  }

  isNoEventsFoundVisible() {
    return (this.events && (this.events.length === 0));
  }

  isEventsListVisible() {
    return (this.events && (this.events.length > 0));
  }
}
