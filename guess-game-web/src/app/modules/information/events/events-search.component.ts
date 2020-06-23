import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type.model';

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

  constructor() {
  }

  ngOnInit(): void {
  }

  onEventTypeChange(eventType: EventType) {
    // TODO: implement
  }

  onEventTypeKindChange(checked: boolean) {
    // TODO: implement
  }

  onLanguageChange() {
    // TODO: implement
  }
}
