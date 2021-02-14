import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Event } from '../../shared/models/event/event.model';
import { HomeState } from '../../shared/models/home-state.model';
import { EventService } from '../../shared/services/event.service';
import { LocaleService } from '../../shared/services/locale.service';
import { getEventDates } from '../general/utility-functions';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  public imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public event: Event;
  public eventDates: string;
  public homeState = HomeState.LoadingState;

  constructor(private eventService: EventService, public translateService: TranslateService, localeService: LocaleService) {
  }

  ngOnInit(): void {
    this.loadDefaultEvent();
  }

  loadDefaultEvent() {
    this.eventService.getDefaultEventHomeInfo()
      .subscribe(data => {
        this.event = data;
        this.eventDates = (this.event) ? getEventDates(this.event, this.translateService) : null;
        this.homeState = (this.event) ? HomeState.DefaultStateFoundState : HomeState.DefaultStateNotFoundState;
      });
  }

  onLanguageChange() {
    this.loadDefaultEvent();
  }

  isLoading(): boolean {
    return (this.homeState === HomeState.LoadingState);
  }

  isDefaultEventFound(): boolean {
    return (this.homeState === HomeState.DefaultStateFoundState);
  }

  isDefaultEventNotFound(): boolean {
    return (this.homeState === HomeState.DefaultStateNotFoundState);
  }
}
