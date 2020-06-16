import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type.model';
import { EventStatistics } from '../../../shared/models/event-statistics.model';
import { StatisticsService } from '../../../shared/services/statistics.service';
import { QuestionService } from '../../../shared/services/question.service';
import { findEventTypeByDefaultEvent } from '../../general/utility-functions';

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

  public multiSortMeta: any[] = [];

  constructor(private statisticsService: StatisticsService, private questionService: QuestionService,
              public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'name', order: 1});
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
              const selectedConference = findEventTypeByDefaultEvent(defaultEventData, this.conferences);

              if (selectedConference) {
                this.selectedConference = selectedConference;
              } else {
                this.selectedConference = null;
              }

              this.loadEventStatistics(this.selectedConference);
            });
        } else {
          this.selectedConference = null;
          this.loadEventStatistics(this.selectedConference);
        }
      });
  }

  loadEventStatistics(conference: EventType) {
    this.statisticsService.getEventStatistics(conference)
      .subscribe(data => {
          this.eventStatistics = data;
        }
      );
  }

  onConferenceChange(conference: EventType) {
    this.loadEventStatistics(conference);
  }

  onLanguageChange() {
    this.loadEventStatistics(this.selectedConference);
  }
}
