import { Component, OnInit } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";
import { MenuItem } from "primeng/api";

@Component({
  selector: 'app-statistics-tabmenu',
  templateUrl: './statistics-tabmenu.component.html'
})
export class StatisticsTabMenuComponent implements OnInit {
  private readonly STATISTICS_EVENT_TYPES_TITLE_KEY = 'statistics.eventTypes.title';
  private readonly STATISTICS_EVENT_TITLE_KEY = 'statistics.events.title';
  private readonly STATISTICS_SPEAKERS_TITLE_KEY = 'statistics.speakers.title';
  private readonly STATISTICS_COMPANIES_TITLE_KEY = 'statistics.companies.title';

  private readonly KEYS = [this.STATISTICS_EVENT_TYPES_TITLE_KEY, this.STATISTICS_EVENT_TITLE_KEY,
    this.STATISTICS_SPEAKERS_TITLE_KEY, this.STATISTICS_COMPANIES_TITLE_KEY];

  public items: MenuItem[] = [];

  constructor(public translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.initMenuItems();

    this.translateService.onLangChange
      .subscribe(() => {
        this.initMenuItems();
      });
  }

  initMenuItems() {
    this.translateService.get(this.KEYS)
      .subscribe(labels => {
        this.items = [
          {label: labels[this.STATISTICS_EVENT_TYPES_TITLE_KEY], routerLink: '/information/statistics/event-types'},
          {label: labels[this.STATISTICS_EVENT_TITLE_KEY], routerLink: '/information/statistics/events'},
          {label: labels[this.STATISTICS_SPEAKERS_TITLE_KEY], routerLink: '/information/statistics/speakers'},
          {label: labels[this.STATISTICS_COMPANIES_TITLE_KEY], routerLink: '/information/statistics/companies'},
          {label: 'OLAP', routerLink: '/information/statistics/olap'}
        ];
      });
  }
}
