import { Component, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { formatDate } from "@angular/common";
import { TranslateService } from "@ngx-translate/core";
import { QuestionSet } from "../../shared/models/question-set.model";
import { StartParameters } from "../../shared/models/start-parameters.model";
import { GuessMode } from "../../shared/models/guess-type.model";
import { EventType } from "../../shared/models/event-type.model";
import { Event } from "../../shared/models/event.model";
import { QuestionService } from "../../shared/services/question.service";
import { StateService } from "../../shared/services/state.service";

@Component({
  selector: 'app-start',
  templateUrl: './start.component.html'
})
export class StartComponent implements OnInit {
  public questionSets: QuestionSet[] = [];          //TODO: delete
  public selectedQuestionSets: QuestionSet[] = [];  //TODO: delete

  public eventTypes: EventType[];
  public selectedEventTypes: EventType[] = [];

  public events: Event[];
  public selectedEvents = [];

  public guessMode = GuessMode;
  public selectedGuessMode: GuessMode = GuessMode.GuessNameByPhotoMode;

  public quantities: number[] = [];
  public selectedQuantity: number;

  private defaultEvent: Event;

  constructor(private questionService: QuestionService, private stateService: StateService, private router: Router,
              public translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.loadQuestionSets();  //TODO: delete
    this.loadEventTypes();
  }

  //TODO: delete
  loadQuestionSets() {
    this.questionService.getQuestionSets()
      .subscribe(data => {
        this.questionSets = data;

        if (this.questionSets.length > 0) {
          this.questionService.getDefaultQuestionSetId()
            .subscribe(data => {
              let defaultQuestionSetId = data;
              if ((defaultQuestionSetId < 0) || (defaultQuestionSetId >= this.questionSets.length)) {
                defaultQuestionSetId = 0;
              }

              this.selectedQuestionSets = [this.questionSets[defaultQuestionSetId]];
              this.loadQuantities(this.selectedQuestionSets, this.selectedGuessMode);
            });
        }
      });
  }

  //TODO: delete
  onSetChange(questionSets: QuestionSet[]) {
    this.loadQuantities(questionSets, this.selectedGuessMode);
  }

  loadEventTypes() {
    this.questionService.getEventTypes()
      .subscribe(data => {
        this.eventTypes = data;

        if (this.eventTypes.length > 0) {
          this.questionService.getDefaultEvent()
            .subscribe(data => {
              this.defaultEvent = data;

              let selectedEventType = this.findEventTypeByDefaultEvent(this.defaultEvent);

              if (selectedEventType) {
                this.selectedEventTypes = [selectedEventType];
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

  findEventTypeByDefaultEvent(defaultEvent: Event): Event {
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
            this.selectedEvents = [this.events[this.events.length - 1]];
          }
        } else {
          this.selectedEvents = [];
        }

        this.loadQuantities2(this.selectedEventTypes, this.selectedEvents, this.selectedGuessMode);
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
    this.loadQuantities2(this.selectedEventTypes, events, this.selectedGuessMode);
  }

  onModeChange(guessMode: string) {
    this.loadQuantities(this.selectedQuestionSets, guessMode);  //TODO: delete
    this.loadQuantities2(this.selectedEventTypes, this.selectedEvents, guessMode);
  }

  //TODO: delete
  loadQuantities(questionSets: QuestionSet[], guessMode: string) {
    this.questionService.getQuantities(questionSets.map(s => s.id), guessMode)
      .subscribe(data => {
        this.quantities = data;

        if (this.quantities.length > 0) {
          this.selectedQuantity = this.quantities[this.quantities.length - 1];
        } else {
          this.selectedQuantity = 0;
        }
      });
  }

  //TODO: rename
  loadQuantities2(eventTypes: EventType[], events: Event[], guessMode) {
    //TODO: implement
    console.log('(loadQuantities) eventTypes: ' + JSON.stringify(eventTypes) + '; events: ' + JSON.stringify(events) + '; guessMode: ' + guessMode);
  }

  start() {
    this.stateService.setStartParameters(
      new StartParameters(
        this.selectedQuestionSets.map(s => s.id),
        this.selectedQuantity,
        this.selectedGuessMode))
      .subscribe(data => {
        this.router.navigateByUrl('/guess/name');
      });
  }

  isEventsDisabled(): boolean {
    return (this.selectedEventTypes &&
      ((this.selectedEventTypes.length > 1) || ((this.selectedEventTypes.length === 1) && !this.selectedEventTypes[0].conference)));
  }

  isStartDisabled(): boolean {
    return (this.selectedQuestionSets && (this.selectedQuestionSets.length <= 0)) ||
      (this.selectedQuantity == 0);
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
    this.loadQuestionSets();
    this.loadEventTypes();
  }
}
