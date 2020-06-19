import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type.model';
import { Event } from '../../../shared/models/event.model';
import { EventTypeService } from '../../../shared/services/event-type.service';

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

  public talkName: string;
  public speakerName: string;

  private searched = false;

  constructor(private eventTypeService: EventTypeService) {
  }

  ngOnInit(): void {
    this.loadEventTypes();
  }

  loadEventTypes() {
    this.eventTypeService.getEventTypes()
      .subscribe(eventTypesData => {
        this.eventTypes = eventTypesData;
        this.eventTypeSelectItems = this.eventTypes.map(et => {
            return {label: et.displayName, value: et};
          }
        );

        if (this.eventTypes.length > 0) {
          // TODO: implement
        } else {
          this.selectedEventType = null;
          this.loadEvents(this.selectedEventType);
        }
      });
  }

  loadEvents(eventType: EventType) {
    // TODO: implement
  }

  onLanguageChange() {
    this.search();
  }

  onEventTypeChange(eventType: EventType) {
    // TODO: implement
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
