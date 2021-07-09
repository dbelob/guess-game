import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type/event-type.model';
import { Organizer } from '../../../shared/models/organizer/organizer.model';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { OrganizerService } from '../../../shared/services/organizer.service';
import { findOrganizerById, getEventTypesWithSortName } from '../../general/utility-functions';

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

  constructor(private eventTypeService: EventTypeService, public organizerService: OrganizerService,
              public translateService: TranslateService) {
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

  loadOrganizers() {
    this.organizerService.getOrganizers()
      .subscribe(organizerData => {
        this.fillOrganizers(organizerData);

        if (this.organizers.length > 0) {
          this.organizerService.getDefaultEventOrganizer()
            .subscribe(defaultOrganizerData => {
              const selectedOrganizer = (defaultOrganizerData) ? findOrganizerById(defaultOrganizerData.id, this.organizers) : null;
              this.selectedOrganizer = (selectedOrganizer) ? selectedOrganizer : null;

              this.loadEventTypes(this.isConferences, this.isMeetups, this.selectedOrganizer);
            });
        } else {
          this.selectedOrganizer = null;
          this.loadEventTypes(this.isConferences, this.isMeetups, this.selectedOrganizer);
        }
      });
  }

  loadEventTypes(isConferences: boolean, isMeetups: boolean, organizer: Organizer) {
    this.eventTypeService.getEventTypes(isConferences, isMeetups, organizer)
      .subscribe(data => {
        this.eventTypes = getEventTypesWithSortName(data);
      });
  }

  onEventTypeKindChange() {
    this.loadEventTypes(this.isConferences, this.isMeetups, this.selectedOrganizer);
  }

  onOrganizerChange(organizer: Organizer) {
    this.loadEventTypes(this.isConferences, this.isMeetups, organizer);
  }

  onLanguageChange() {
    const currentSelectedOrganizer = this.selectedOrganizer;

    this.organizerService.getOrganizers()
      .subscribe(organizerData => {
        this.fillOrganizers(organizerData);

        if (this.organizers.length > 0) {
          this.selectedOrganizer = (currentSelectedOrganizer) ? findOrganizerById(currentSelectedOrganizer.id, this.organizers) : null;
        } else {
          this.selectedOrganizer = null;
        }

        this.loadEventTypes(this.isConferences, this.isMeetups, this.selectedOrganizer);
      });
  }

  isNoEventTypesFoundVisible() {
    return (this.eventTypes && (this.eventTypes.length === 0));
  }

  isEventTypesListVisible() {
    return (this.eventTypes && (this.eventTypes.length > 0));
  }
}
