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
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  private id: number;
  public eventDetails: EventDetails;

  constructor(private eventService: EventService, public translateService: TranslateService,
              private activatedRoute: ActivatedRoute) {
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
}
