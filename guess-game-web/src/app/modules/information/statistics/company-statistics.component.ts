import { Component, OnInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { EventType } from '../../../shared/models/event-type/event-type.model';
import { CompanyStatistics } from '../../../shared/models/statistics/company-statistics.model';
import { StatisticsService } from '../../../shared/services/statistics.service';
import { EventTypeService } from '../../../shared/services/event-type.service';
import { EventService } from '../../../shared/services/event.service';
import { findEventTypeById } from '../../general/utility-functions';

@Component({
  selector: 'app-company-statistics',
  templateUrl: './company-statistics.component.html'
})
export class CompanyStatisticsComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public isConferences = true;
  public isMeetups = true;

  public eventTypes: EventType[] = [];
  public selectedEventType: EventType;
  public eventTypeSelectItems: SelectItem[] = [];

  public companyStatistics = new CompanyStatistics();
  public multiSortMeta: any[] = [];

  constructor(private statisticsService: StatisticsService, private eventTypeService: EventTypeService,
              private eventService: EventService, public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'talksQuantity', order: -1});
    this.multiSortMeta.push({field: 'eventsQuantity', order: -1});
    this.multiSortMeta.push({field: 'eventTypesQuantity', order: -1});
  }

  ngOnInit(): void {
    this.loadEventTypes(this.isConferences, this.isMeetups);
  }

  loadEventTypes(isConferences: boolean, isMeetups: boolean) {
    this.eventTypeService.getFilterEventTypes(isConferences, isMeetups)
      .subscribe(eventTypesData => {
        this.eventTypes = eventTypesData;
        this.eventTypeSelectItems = this.eventTypes.map(et => {
            return {label: et.name, value: et};
          }
        );

        if (this.eventTypes.length > 0) {
          this.eventService.getDefaultEvent()
            .subscribe(defaultEventData => {
              const selectedEventType = (defaultEventData) ? findEventTypeById(defaultEventData.eventTypeId, this.eventTypes) : null;

              if (selectedEventType) {
                this.selectedEventType = selectedEventType;
              } else {
                this.selectedEventType = null;
              }

              this.loadCompanyStatistics(this.selectedEventType);
            });
        } else {
          this.selectedEventType = null;
          this.loadCompanyStatistics(this.selectedEventType);
        }
      });
  }

  loadCompanyStatistics(eventType: EventType) {
    this.statisticsService.getCompanyStatistics(this.isConferences, this.isMeetups, eventType)
      .subscribe(data => {
          this.companyStatistics = data;
        }
      );
  }

  onEventTypeChange(eventType: EventType) {
    this.loadCompanyStatistics(eventType);
  }

  onEventTypeKindChange(checked: boolean) {
    this.loadEventTypes(this.isConferences, this.isMeetups);
  }

  onLanguageChange() {
    this.loadCompanyStatistics(this.selectedEventType);
  }

  isNoCompaniesFoundVisible() {
    return (this.companyStatistics?.companyMetricsList && (this.companyStatistics.companyMetricsList.length === 0));
  }

  isCompaniesListVisible() {
    return (this.companyStatistics?.companyMetricsList && (this.companyStatistics.companyMetricsList.length > 0));
  }
}
