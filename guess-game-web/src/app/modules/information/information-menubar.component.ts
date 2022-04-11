import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-information-menubar',
  templateUrl: './information-menubar.component.html'
})
export class InformationMenubarComponent implements OnInit {
  private readonly EVENT_TYPES_TITLE_KEY = 'eventTypes.title';
  private readonly EVENTS_TITLE_KEY = 'events.title';
  private readonly TALKS_TITLE_KEY = 'talks.title';
  private readonly SPEAKERS_TITLE_KEY = 'speakers.title';
  private readonly COMPANIES_TITLE_KEY = 'companies.title';
  private readonly STATISTICS_TITLE_KEY = 'statistics.title';

  private readonly KEYS = [this.EVENT_TYPES_TITLE_KEY, this.EVENTS_TITLE_KEY, this.TALKS_TITLE_KEY,
    this.SPEAKERS_TITLE_KEY, this.COMPANIES_TITLE_KEY, this.STATISTICS_TITLE_KEY];

  @Input() private type: string;

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
          {label: labels[this.EVENT_TYPES_TITLE_KEY], routerLink: '/information/event-types'},
          {label: labels[this.EVENTS_TITLE_KEY], routerLink: '/information/events'},
          {label: labels[this.TALKS_TITLE_KEY], routerLink: '/information/talks'},
          {label: labels[this.SPEAKERS_TITLE_KEY], routerLink: '/information/speakers'},
          {label: labels[this.COMPANIES_TITLE_KEY], routerLink: '/information/companies'},
          {label: labels[this.STATISTICS_TITLE_KEY], routerLink: '/information/statistics'}
        ];
      });
  }

  isEventTypes(): boolean {
    return ('eventTypes' === this.type);
  }

  isEvents(): boolean {
    return ('events' === this.type);
  }

  isTalks(): boolean {
    return ('talks' === this.type);
  }

  isSpeakers(): boolean {
    return ('speakers' === this.type);
  }

  isCompanies(): boolean {
    return ('companies' === this.type);
  }

  isStatistics(): boolean {
    return ('statistics' === this.type);
  }
}
