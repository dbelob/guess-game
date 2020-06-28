import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { EventType } from '../../../shared/models/event-type.model';
import { EventTypeService } from '../../../shared/services/event-type.service';

@Component({
  selector: 'app-event-types-search',
  templateUrl: './event-types-search.component.html'
})
export class EventTypesSearchComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public isConferences = true;
  public isMeetups = true;

  public eventTypes: EventType[] = [];
  public multiSortMeta: any[] = [];

  constructor(private eventTypeService: EventTypeService, public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'displayName', order: 1});
  }

  ngOnInit(): void {
    this.loadEventTypes(this.isConferences, this.isMeetups);
  }

  loadEventTypes(isConferences: boolean, isMeetups: boolean) {
    this.eventTypeService.getEventTypes(isConferences, isMeetups)
      .subscribe(data => {
        this.eventTypes = data;
      });
  }

  onEventTypeKindChange(checked: boolean) {
    this.loadEventTypes(this.isConferences, this.isMeetups);
  }

  onLanguageChange() {
    this.loadEventTypes(this.isConferences, this.isMeetups);
  }

  isNoEventTypesFoundVisible() {
    return (this.eventTypes && (this.eventTypes.length === 0));
  }

  isEventTypesListVisible() {
    return (this.eventTypes && (this.eventTypes.length > 0));
  }
}
