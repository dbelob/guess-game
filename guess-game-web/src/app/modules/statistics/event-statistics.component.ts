import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../shared/models/event-type.model';
import { EventStatistics } from '../../shared/models/event-statistics.model';
import { Event } from "../../shared/models/event.model";
import { StatisticsService } from '../../shared/services/statistics.service';
import { QuestionService } from "../../shared/services/question.service";

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

  constructor(private statisticsService: StatisticsService, private questionService: QuestionService,
              public translateService: TranslateService, private router: Router) {
  }

  ngOnInit(): void {
    this.loadConferences();
  }

  loadConferences() {
    this.statisticsService.getConferences()
      .subscribe(conferenceData => {
        this.conferences = conferenceData;
        this.conferenceSelectItems = this.conferences.map(et => {
            return {label: et.name, value: et};
          }
        );

        if (this.conferences.length > 0) {
          this.questionService.getDefaultEvent()
            .subscribe(defaultEventData => {
              const selectedConference = this.findConferenceByDefaultEvent(defaultEventData);

              if (selectedConference) {
                this.selectedConference = selectedConference;
              }

              this.loadEventStatistics(this.selectedConference);
            });
        } else {
          this.selectedConference = null;
          this.loadEventStatistics(this.selectedConference);
        }
      });
  }

  findConferenceByDefaultEvent(defaultEvent: Event): EventType {
    if (defaultEvent) {
      for (let i = 0; i < this.conferences.length; i++) {
        const eventType: EventType = this.conferences[i];

        if (defaultEvent.eventTypeId === eventType.id) {
          return eventType;
        }
      }
    }

    return null;
  }

  onConferenceChange(conference: EventType) {
    this.loadEventStatistics(conference);
  }

  loadEventStatistics(conference: EventType) {
    this.statisticsService.getEventStatistics(conference)
      .subscribe(data => {
          this.eventStatistics = data;
        }
      );
  }

  onLanguageChange() {
    this.loadEventStatistics(this.selectedConference);
  }
}
