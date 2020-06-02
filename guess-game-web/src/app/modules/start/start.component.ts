import { Component, ElementRef, OnInit, QueryList, ViewChildren } from '@angular/core';
import { Router } from "@angular/router";
import { formatDate } from "@angular/common";
import { TranslateService } from "@ngx-translate/core";
import { SelectItem } from "primeng/api";
import { StartParameters } from "../../shared/models/start-parameters.model";
import { GuessMode } from "../../shared/models/guess-mode.model";
import { EventType } from "../../shared/models/event-type.model";
import { Event } from "../../shared/models/event.model";
import { QuestionService } from "../../shared/services/question.service";
import { StateService } from "../../shared/services/state.service";

@Component({
  selector: 'app-start',
  templateUrl: './start.component.html'
})
export class StartComponent implements OnInit {
  private MIN_QUANTITY_VALUE = 4;

  private imageDirectory: string = 'assets/images';
  public eventsImageDirectory: string = `${this.imageDirectory}/events`;

  public eventTypes: EventType[] = [];
  public selectedEventTypes: EventType[] = [];
  public eventTypeSelectItems: SelectItem[] = [];

  public events: Event[] = [];
  public selectedEvents: Event[] = [];

  public guessMode = GuessMode;
  public selectedGuessMode: GuessMode = GuessMode.GuessNameByPhotoMode;

  public quantities: number[] = [];
  public selectedQuantity: number;
  public quantitySelectItems: SelectItem[] = [];

  private defaultEvent: Event;

  @ViewChildren("eventTypeRow", {read: ElementRef}) rowElement: QueryList<ElementRef>;

  constructor(private questionService: QuestionService, private stateService: StateService, private router: Router,
              public translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.loadEventTypes();
  }

  loadEventTypes() {
    this.questionService.getEventTypes()
      .subscribe(data => {
        this.eventTypes = data;
        this.eventTypeSelectItems = this.eventTypes.map(et => {
            return {label: et.displayName, value: et}
          }
        );

        if (this.eventTypes.length > 0) {
          this.questionService.getDefaultEvent()
            .subscribe(data => {
              this.defaultEvent = data;

              let selectedEventType = this.findEventTypeByDefaultEvent(this.defaultEvent);

              if (selectedEventType) {
                this.selectedEventTypes = [selectedEventType];
                this.scrollIntoSelectedEventType(selectedEventType);
              } else {
                this.selectedEventTypes = [this.eventTypes[0]];
              }

              this.loadEvents(this.selectedEventTypes);
            });
        } else {
          this.selectedEventTypes = [];
          this.loadEvents(this.selectedEventTypes);
        }
      });
  }

  findEventTypeByDefaultEvent(defaultEvent: Event): EventType {
    if (defaultEvent) {
      for (let i = 0; i < this.eventTypes.length; i++) {
        let eventType: EventType = this.eventTypes[i];

        if (defaultEvent.eventTypeId === eventType.id) {
          return eventType;
        }
      }
    }

    return null;
  }

  scrollIntoSelectedEventType(eventType: EventType) {
    const elementRef = this.rowElement.find(r => r.nativeElement.getAttribute('id') == eventType.id);

    if (elementRef) {
      elementRef.nativeElement.scrollIntoView({behavior: 'auto', block: 'center', inline: 'nearest'});
    }
  }

  onEventTypeChange(eventTypes: EventType[]) {
    this.loadEvents(eventTypes);
  }

  loadEvents(eventTypes: EventType[]) {
    this.questionService.getEvents(eventTypes.map(et => et.id))
      .subscribe(data => {
        this.events = this.getEventsWithDisplayName(data);

        if (this.events.length > 0) {
          let selectedEvent = this.findEventByDefaultEvent(this.defaultEvent);

          if (selectedEvent) {
            this.selectedEvents = [selectedEvent];
          } else {
            this.selectedEvents = [this.events[0]];
          }
        } else {
          this.selectedEvents = [];
        }

        this.loadQuantities(this.selectedEventTypes, this.selectedEvents, this.selectedGuessMode);
      });
  }

  getEventsWithDisplayName(events: Event[]): Event[] {
    if (events) {
      for (let i = 0; i < events.length; i++) {
        let event: Event = events[i];
        let isEventDateParenthesesVisible = this.isEventDateParenthesesVisible(event);
        let isEventStartDateVisible = this.isEventStartDateVisible(event);
        let isEventHyphenVisible = this.isEventHyphenVisible(event);
        let isEventEndDateVisible = this.isEventEndDateVisible(event);

        let displayName = event.name;

        if (isEventDateParenthesesVisible) {
          displayName += ' (';
        }

        if (isEventStartDateVisible) {
          displayName += formatDate(event.startDate, 'shortDate', this.translateService.currentLang, undefined);
        }

        if (isEventHyphenVisible) {
          displayName += ' – ';
        }

        if (isEventEndDateVisible) {
          displayName += formatDate(event.endDate, 'shortDate', this.translateService.currentLang, undefined);
        }

        if (isEventDateParenthesesVisible) {
          displayName += ')';
        }

        event.displayName = displayName;
      }
    }

    return events;
  }

  findEventByDefaultEvent(defaultEvent: Event): Event {
    if (defaultEvent) {
      for (let i = 0; i < this.events.length; i++) {
        let event: Event = this.events[i];

        if (defaultEvent.id === event.id) {
          return event;
        }
      }
    }

    return null;
  }

  onEventChange(events: Event[]) {
    this.loadQuantities(this.selectedEventTypes, events, this.selectedGuessMode);
  }

  onModeChange(guessMode: string) {
    this.loadQuantities(this.selectedEventTypes, this.selectedEvents, guessMode);
  }

  loadQuantities(eventTypes: EventType[], events: Event[], guessMode) {
    this.questionService.getQuantities(eventTypes.map(et => et.id), events.map(e => e.id), guessMode)
      .subscribe(data => {
        this.quantities = data;
        this.quantitySelectItems = this.quantities.map(q => {
            return {label: q.toString(), value: q}
          }
        );

        if (this.quantities.length > 0) {
          this.selectedQuantity = this.quantities[this.quantities.length - 1];
        } else {
          this.selectedQuantity = 0;
        }
      });
  }

  start() {
    this.stateService.setStartParameters(
      new StartParameters(
        this.selectedEventTypes.map(et => et.id),
        this.selectedEvents.map(e => e.id),
        this.selectedGuessMode,
        this.selectedQuantity))
      .subscribe(data => {
        this.router.navigateByUrl('/guess/name-by-photo');
      });
  }

  isEventsDisabled(): boolean {
    return (this.selectedEventTypes &&
      ((this.selectedEventTypes.length > 1) || ((this.selectedEventTypes.length === 1) && !this.selectedEventTypes[0].conference)));
  }

  isStartDisabled(): boolean {
    return (!this.selectedEventTypes) || (!this.selectedEvents) ||
      (this.selectedEventTypes && (this.selectedEventTypes.length <= 0)) ||
      (this.selectedEvents && (this.selectedEvents.length <= 0)) ||
      (this.selectedQuantity < this.MIN_QUANTITY_VALUE);
  }

  isEventStartDateVisible(event: Event): boolean {
    return !!event.startDate;
  }

  isEventEndDateVisible(event: Event): boolean {
    return (event.startDate && event.endDate && (event.startDate !== event.endDate));
  }

  isEventDateParenthesesVisible(event: Event): boolean {
    return (this.isEventStartDateVisible(event) || this.isEventEndDateVisible(event));
  }

  isEventHyphenVisible(event: Event): boolean {
    return (this.isEventStartDateVisible(event) && this.isEventEndDateVisible(event));
  }

  onLanguageChange() {
    this.loadEventTypes();
  }
}
