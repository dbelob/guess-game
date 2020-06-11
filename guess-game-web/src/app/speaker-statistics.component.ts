import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { EventType } from './shared/models/event-type.model';

@Component({
  selector: 'app-speaker-statistics',
  templateUrl: './speaker-statistics.component.html'
})
export class SpeakerStatisticsComponent implements OnInit {
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

  onEventTypeChange(conference: EventType) {
    // TODO: implement
  }

  onLanguageChange() {
    // TODO: implement
  }

  onEventTypeKindChange(checked: boolean) {
    // TODO: implement
  }
}
