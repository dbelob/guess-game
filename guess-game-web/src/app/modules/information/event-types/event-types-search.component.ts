import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type/event-type.model';
import { Organizer } from '../../../shared/models/organizer/organizer.model';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { getEventTypesWithSortName } from '../../general/utility-functions';

@Component({
  selector: 'app-event-types-search',
  templateUrl: './event-types-search.component.html'
})
export class EventTypesSearchComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public isConferences = true;
  public isMeetups = true;

  public organizers: Organizer[] = [];
  public selectedOrganizer: Organizer;
  public organizerSelectItems: SelectItem[] = [];

  public eventTypes: EventType[] = [];
  public multiSortMeta: any[] = [];

  constructor(private eventTypeService: EventTypeService, public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'sortName', order: 1});
  }

  ngOnInit(): void {
    this.loadOrganizers(this.isConferences, this.isMeetups);
  }

  loadOrganizers(isConferences: boolean, isMeetups: boolean) {
    // TODO: implement
    this.loadEventTypes(this.selectedOrganizer, this.isConferences, this.isMeetups);
  }

  loadEventTypes(organizer: Organizer, isConferences: boolean, isMeetups: boolean) {
    this.eventTypeService.getEventTypes(isConferences, isMeetups)
      .subscribe(data => {
        this.eventTypes = getEventTypesWithSortName(data);
      });
  }

  onOrganizerChange(organizer: Organizer) {
    this.loadEventTypes(organizer, this.isConferences, this.isMeetups);
  }

  onEventTypeKindChange() {
    this.loadEventTypes(this.selectedOrganizer, this.isConferences, this.isMeetups);
  }

  onLanguageChange() {
    this.loadOrganizers(this.isConferences, this.isMeetups);
  }

  isNoEventTypesFoundVisible() {
    return (this.eventTypes && (this.eventTypes.length === 0));
  }

  isEventTypesListVisible() {
    return (this.eventTypes && (this.eventTypes.length > 0));
  }
}
