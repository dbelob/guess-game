import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type.model';
import { Event } from '../../../shared/models/event.model';

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

  constructor() {
  }

  ngOnInit(): void {
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
