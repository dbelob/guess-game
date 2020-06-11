import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../shared/models/event-type.model';
import { EventStatistics } from '../../shared/models/event-statistics.model';
import { StatisticsService } from '../../shared/services/statistics.service';

@Component({
  selector: 'app-event-statistics',
  templateUrl: './event-statistics.component.html'
})
export class EventStatisticsComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public conferences: EventType[] = [];
  public selectedConference: EventType;
  public conferenceSelectItems: SelectItem[] = [];

  public eventStatistics = new EventStatistics();

  constructor(private statisticsService: StatisticsService, public translateService: TranslateService, private router: Router) {
  }

  ngOnInit(): void {
    this.loadConferences();
    this.loadEventStatistics();
  }

  loadConferences() {
    this.statisticsService.getConferences()
      .subscribe(data => {
        this.conferences = data;
        this.conferenceSelectItems = this.conferences.map(et => {
            return {label: et.name, value: et};
          }
        );
      });
  }

  onConferenceChange(conference: EventType) {
    this.loadEventStatistics();
  }

  loadEventStatistics() {
    this.statisticsService.getEventStatistics(this.selectedConference)
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
