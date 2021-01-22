import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SelectItem } from 'primeng/api';
import { EventTypeStatistics } from '../../../shared/models/statistics/event-type-statistics.model';
import { Organizer } from '../../../shared/models/organizer/organizer.model';
import { StatisticsService } from '../../../shared/services/statistics.service';
import { OrganizerService } from '../../../shared/services/organizer.service';
import { findOrganizerById, getEventTypeStatisticsWithSortName } from '../../general/utility-functions';

@Component({
  selector: 'app-event-type-statistics',
  templateUrl: './event-type-statistics.component.html'
})
export class EventTypeStatisticsComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public isConferences = true;
  public isMeetups = true;

  public organizers: Organizer[] = [];
  public selectedOrganizer: Organizer;
  public organizerSelectItems: SelectItem[] = [];

  public eventTypeStatistics = new EventTypeStatistics();
  public multiSortMeta: any[] = [];

  constructor(private statisticsService: StatisticsService, public organizerService: OrganizerService,
              public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'sortName', order: 1});
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
          this.organizerService.getDefaultOrganizer()
            .subscribe(defaultOrganizerData => {
              const selectedOrganizer = (defaultOrganizerData) ? findOrganizerById(defaultOrganizerData.id, this.organizers) : null;
              this.selectedOrganizer = (selectedOrganizer) ? selectedOrganizer : null;

              this.loadEventTypeStatistics(this.isConferences, this.isMeetups, this.selectedOrganizer);
            });
        } else {
          this.selectedOrganizer = null;
          this.loadEventTypeStatistics(this.isConferences, this.isMeetups, this.selectedOrganizer);
        }
      });
  }

  loadEventTypeStatistics(isConferences: boolean, isMeetups: boolean, organizer: Organizer) {
    this.statisticsService.getEventTypeStatistics(isConferences, isMeetups, organizer)
      .subscribe(data => {
          this.eventTypeStatistics = getEventTypeStatisticsWithSortName(data);
        }
      );
  }

  onEventTypeKindChange() {
    this.loadEventTypeStatistics(this.isConferences, this.isMeetups, this.selectedOrganizer);
  }

  onOrganizerChange(organizer: Organizer) {
    this.loadEventTypeStatistics(this.isConferences, this.isMeetups, organizer);
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

        this.loadEventTypeStatistics(this.isConferences, this.isMeetups, this.selectedOrganizer);
      });
  }

  isNoEventTypesFoundVisible() {
    return (this.eventTypeStatistics?.eventTypeMetricsList && (this.eventTypeStatistics.eventTypeMetricsList.length === 0));
  }

  isEventTypesListVisible() {
    return (this.eventTypeStatistics?.eventTypeMetricsList && (this.eventTypeStatistics.eventTypeMetricsList.length > 0));
  }
}
