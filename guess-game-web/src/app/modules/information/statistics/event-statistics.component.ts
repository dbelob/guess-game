import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SelectItem } from 'primeng/api';
import { EventType } from '../../../shared/models/event-type/event-type.model';
import { EventService } from '../../../shared/services/event.service';
import { EventStatistics } from '../../../shared/models/statistics/event-statistics.model';
import { QuestionService } from '../../../shared/services/question.service';
import { StatisticsService } from '../../../shared/services/statistics.service';
import { findEventTypeById } from '../../general/utility-functions';

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
              private eventService: EventService, public translateService: TranslateService) {
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
          this.eventService.getDefaultEvent()
            .subscribe(defaultEventData => {
              const selectedConference = (defaultEventData) ? findEventTypeById(defaultEventData.eventTypeId, this.conferences) : null;

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

  isNoEventsFoundVisible() {
    return (this.eventStatistics?.eventMetricsList && (this.eventStatistics.eventMetricsList.length === 0));
  }

  isEventsListVisible() {
    return (this.eventStatistics?.eventMetricsList && (this.eventStatistics.eventMetricsList.length > 0));
  }
}
