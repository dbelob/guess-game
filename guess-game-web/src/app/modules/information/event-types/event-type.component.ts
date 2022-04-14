import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { EventTypeDetails } from '../../../shared/models/event-type/event-type-details.model';
import { EventTypeService } from '../../../shared/services/event-type.service';

@Component({
  selector: 'app-event-type',
  templateUrl: './event-type.component.html'
})
export class EventTypeComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  public id: number;
  public eventTypeDetails: EventTypeDetails = new EventTypeDetails();
  public multiSortMeta: any[] = [];

  constructor(private eventTypeService: EventTypeService, public translateService: TranslateService,
              private activatedRoute: ActivatedRoute) {
    this.multiSortMeta.push({field: 'startDate', order: -1});
    this.multiSortMeta.push({field: 'endDate', order: -1});
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const idString: string = params['id'];
      const idNumber: number = Number(idString);

      if (!isNaN(idNumber)) {
        this.id = idNumber;
        this.loadEventType(this.id);
      }
    });
  }

  loadEventType(id: number) {
    this.eventTypeService.getEventType(id)
      .subscribe(data => {
        this.eventTypeDetails = data;
      });
  }

  onLanguageChange() {
    this.loadEventType(this.id);
  }

  isEventTypeLinksVisible() {
    return this.eventTypeDetails.eventType?.siteLink || this.eventTypeDetails.eventType?.facebookLink ||
      this.eventTypeDetails.eventType?.vkLink || this.eventTypeDetails.eventType?.twitterLink ||
      this.eventTypeDetails.eventType?.youtubeLink || this.eventTypeDetails.eventType?.telegramLink ||
      this.eventTypeDetails.eventType?.speakerdeckLink || this.eventTypeDetails.eventType?.habrLink;
  }

  isEventsListVisible() {
    return ((this.eventTypeDetails.events) && (this.eventTypeDetails.events.length > 0));
  }
}
