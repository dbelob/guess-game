import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type.model';
import { Event } from '../../../shared/models/event.model';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { EventService } from '../../../shared/services/event.service';
import { findEventTypeByDefaultEvent } from '../../general/utility-functions';

@Component({
  selector: 'app-talks-search',
  templateUrl: './talks-search.component.html'
})
export class TalksSearchComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public eventTypes: EventType[] = [];
  public selectedEventType: EventType;
  public eventTypeSelectItems: SelectItem[] = [];

  public events: Event[] = [];
  public selectedEvent: Event;
  public eventSelectItems: SelectItem[] = [];

  private defaultEvent: Event;

  public talkName: string;
  public speakerName: string;

  private searched = false;

  constructor(private eventTypeService: EventTypeService, private eventService: EventService) {
  }

  ngOnInit(): void {
    this.loadEventTypes();
  }

  loadEventTypes() {
    this.eventTypeService.getEventTypes()
      .subscribe(eventTypesData => {
        this.eventTypes = eventTypesData;
        this.eventTypeSelectItems = this.eventTypes.map(et => {
            return {label: et.name, value: et};
          }
        );

        if (this.eventTypes.length > 0) {
          this.eventService.getDefaultEvent()
            .subscribe(defaultEventData => {
              this.defaultEvent = defaultEventData;

              const selectedEventType = findEventTypeByDefaultEvent(this.defaultEvent, this.eventTypes);

              if (selectedEventType) {
                this.selectedEventType = selectedEventType;
              } else {
                this.selectedEventType = this.eventTypes[0];
              }

              this.loadEvents(this.selectedEventType);
            });
        } else {
          this.selectedEventType = null;
          this.loadEvents(this.selectedEventType);
        }
      });
  }

  onEventTypeChange(eventType: EventType) {
    this.loadEvents(eventType);
  }

  loadEvents(eventType: EventType) {
    // TODO: implement
  }

  onLanguageChange() {
    this.search();
  }

  onEventChange(event: Event) {
    // TODO: implement
  }

  onFilterChange(value: any) {
    this.searched = false;
  }

  search() {
    // TODO: implement
  }

  clear() {
    // TODO: implement
  }

  isSearchDisabled(): boolean {
    // TODO: implement
    return false;
  }
}
