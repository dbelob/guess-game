import { Component, OnInit } from '@angular/core';
import { Event } from '../../shared/models/event/event.model';
import { HomeState } from '../../shared/models/home-state.model';
import { EventService } from '../../shared/services/event.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  public imageDirectory = 'assets/images';

  public defaultEvent: Event;
  public homeState = HomeState.LoadingState;

  constructor(private eventService: EventService) {
  }

  ngOnInit(): void {
    this.eventService.getDefaultEventHomeInfo()
      .subscribe(data => {
        this.defaultEvent = data;
        this.homeState = (this.defaultEvent) ? HomeState.DefaultStateFoundState : HomeState.DefaultStateNotFoundState;
      });
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
