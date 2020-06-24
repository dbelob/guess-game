import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { EventDetails } from '../../../shared/models/event-details.model';
import { EventService } from '../../../shared/services/event.service';
import { getEventDisplayName } from '../../general/utility-functions';

@Component({
  selector: 'app-event',
  templateUrl: './event.component.html'
})
export class EventComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public degreesImageDirectory = `${this.imageDirectory}/degrees`;
  public eventsImageDirectory = `${this.imageDirectory}/events`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;
  public twitterUrlPrefix = 'https://twitter.com';
  public gitHubUrlPrefix = 'https://github.com';

  private id: number;
  public eventDetails: EventDetails = new EventDetails();
  public speakersMultiSortMeta: any[] = [];

  constructor(private eventService: EventService, public translateService: TranslateService,
              private activatedRoute: ActivatedRoute) {
    this.speakersMultiSortMeta.push({field: 'displayName', order: 1});
    this.speakersMultiSortMeta.push({field: 'company', order: 1});
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const idString: string = params['id'];
      const idNumber: number = Number(idString);

      if (!isNaN(idNumber)) {
        this.id = idNumber;
        this.loadEvent(this.id);
      }
    });
  }

  loadEvent(id: number) {
    this.eventService.getEvent(id)
      .subscribe(data => {
        this.eventDetails = this.getEventDetailsWithEventDisplayName(data);
      });
  }

  getEventDetailsWithEventDisplayName(eventDetails: EventDetails): EventDetails {
    if (eventDetails?.event) {
      eventDetails.event.displayName = getEventDisplayName(eventDetails.event, this.translateService);
    }

    return eventDetails;
  }

  onLanguageChange() {
    this.loadEvent(this.id);
  }

  isSpeakersListVisible() {
    return ((this.eventDetails.speakers) && (this.eventDetails.speakers.length > 0));
  }

  isTalksListVisible() {
    return ((this.eventDetails.talks) && (this.eventDetails.talks.length > 0));
  }
}
