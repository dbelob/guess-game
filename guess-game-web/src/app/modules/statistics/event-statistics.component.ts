import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { Event } from '../../shared/models/event.model';
import { EventStatistics } from '../../shared/models/event-statistics.model';
import { StatisticsService } from '../../shared/services/statistics.service';

@Component({
  selector: 'app-event-statistics',
  templateUrl: './event-statistics.component.html'
})
export class EventStatisticsComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public selectedEvent: Event;
  public eventStatistics = new EventStatistics();

  constructor(private statisticsService: StatisticsService, public translateService: TranslateService, private router: Router) {
  }

  ngOnInit(): void {
    this.loadEventStatistics();
  }

  loadEventStatistics() {
    this.statisticsService.getEventStatistics(this.selectedEvent ? this.selectedEvent.id : null)
      .subscribe(data => {
          this.eventStatistics = data;
        }
      );
  }

  onLanguageChange() {
    this.loadEventStatistics();
  }

  game() {
    this.router.navigateByUrl('/start');
  }
}
