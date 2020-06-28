import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { EventTypeStatistics } from '../../../shared/models/event-type-statistics.model';
import { StatisticsService } from '../../../shared/services/statistics.service';

@Component({
  selector: 'app-event-type-statistics',
  templateUrl: './event-type-statistics.component.html'
})
export class EventTypeStatisticsComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public isConferences = true;
  public isMeetups = true;
  public eventTypeStatistics = new EventTypeStatistics();

  public multiSortMeta: any[] = [];

  constructor(private statisticsService: StatisticsService, public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'sortName', order: 1});
  }

  ngOnInit(): void {
    this.loadEventTypeStatistics();
  }

  loadEventTypeStatistics() {
    this.statisticsService.getEventTypeStatistics(this.isConferences, this.isMeetups)
      .subscribe(data => {
          this.eventTypeStatistics = data;
        }
      );
  }

  onEventTypeKindChange(checked: boolean) {
    this.loadEventTypeStatistics();
  }

  onLanguageChange() {
    this.loadEventTypeStatistics();
  }

  isNoEventTypesFoundVisible() {
    return (this.eventTypeStatistics?.eventTypeMetricsList && (this.eventTypeStatistics.eventTypeMetricsList.length === 0));
  }

  isEventTypesListVisible() {
    return (this.eventTypeStatistics?.eventTypeMetricsList && (this.eventTypeStatistics.eventTypeMetricsList.length > 0));
  }
}
