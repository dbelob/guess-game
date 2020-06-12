import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { EventTypeStatistics } from '../../shared/models/event-type-statistics.model';
import { StatisticsService } from '../../shared/services/statistics.service';

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

  constructor(private statisticsService: StatisticsService, public translateService: TranslateService) {
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

  onLanguageChange() {
    this.loadEventTypeStatistics();
  }

  onEventTypeKindChange(checked: boolean) {
    this.loadEventTypeStatistics();
  }
}
