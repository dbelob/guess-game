import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type.model';
import { Event } from '../../../shared/models/event.model';
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

  constructor(private eventTypeService: EventTypeService, private eventService: EventService) {
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
              const selectedEventType = findEventTypeById(defaultEventData?.eventTypeId, this.eventTypes);

              if (selectedEventType) {
                this.selectedEventType = selectedEventType;
              } else {
                this.selectedEventType = null;
              }

              this.loadEvents(this.selectedEventType);
            });
        } else {
          this.selectedEventType = null;
          this.loadEvents(this.selectedEventType);
        }
      });
  }

  loadEvents(eventType: EventType) {
    // TODO: implement
    console.log('eventType: ' + JSON.stringify(eventType));
  }

  onEventTypeChange(eventType: EventType) {
    this.loadEvents(eventType);
  }

  onEventTypeKindChange(checked: boolean) {
    this.loadEventTypes(this.isConferences, this.isMeetups);
  }

  onLanguageChange() {
    this.loadEvents(this.selectedEventType);
  }
}
