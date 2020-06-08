import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { EventTypeMetrics } from '../../shared/models/event-type-metrics.model';
import { StatisticsService } from '../../shared/services/statistics.service';

@Component({
  selector: 'app-event-type-statistics',
  templateUrl: './event-type-statistics.component.html'
})
export class EventTypeStatisticsComponent implements OnInit {
  public conferences = true;
  public meetups = true;
  public eventTypeMetrics: EventTypeMetrics[] = [];

  constructor(private statisticsService: StatisticsService, public translateService: TranslateService, private router: Router) {
  }

  ngOnInit(): void {
    this.loadEventTypeStatistics();
  }

  loadEventTypeStatistics() {
    this.statisticsService.getEventTypeMetrics(this.conferences, this.meetups)
      .subscribe(data => {
          this.eventTypeMetrics = data;
          console.log('eventTypeMetrics' + JSON.stringify(this.eventTypeMetrics));
        }
      );
  }

  onLanguageChange() {
    this.loadEventTypeStatistics();
  }

  onEventTypeKindChange(checked: boolean) {
    this.loadEventTypeStatistics();
  }

  game() {
    this.router.navigateByUrl('/start');
  }
}
