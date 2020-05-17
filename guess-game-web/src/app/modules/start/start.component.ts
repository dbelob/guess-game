import { Component, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { TranslateService } from "@ngx-translate/core";
import { QuestionSet } from "../../shared/models/question-set.model";
import { QuestionService } from "../../shared/services/question.service";
import { StateService } from "../../shared/services/state.service";
import { StartParameters } from "../../shared/models/start-parameters.model";
import { GuessType } from "../../shared/models/guess-type.model";
import { EventType } from "../../shared/models/event-type.model";
import { Event } from "../../shared/models/event.model";

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

  public guessType = GuessType;
  public selectedGuessType: GuessType = GuessType.GuessNameType;

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
              this.loadQuantities(this.selectedQuestionSets, this.selectedGuessType);
            });
        }
      });
  }

  //TODO: delete
  onSetChange(questionSets: QuestionSet[]) {
    this.loadQuantities(questionSets, this.selectedGuessType);
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
    if ((eventTypes.length == 1) && eventTypes[0].conference) {
      this.questionService.getEvents(eventTypes[0].id)
        .subscribe(data => {
          this.events = data;

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

          this.loadQuantities2(this.selectedEventTypes, this.selectedEvents, this.selectedGuessType);
        });
    } else {
      this.events = [];
      this.selectedEvents = [];
      this.loadQuantities2(this.selectedEventTypes, this.selectedEvents, this.selectedGuessType);
    }
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
    this.loadQuantities2(this.selectedEventTypes, events, this.selectedGuessType);
  }

  onModeChange(guessType: string) {
    this.loadQuantities(this.selectedQuestionSets, guessType);  //TODO: delete
    this.loadQuantities2(this.selectedEventTypes, this.selectedEvents, guessType);
  }

  //TODO: delete
  loadQuantities(questionSets: QuestionSet[], guessType: string) {
    this.questionService.getQuantities(questionSets.map(s => s.id), guessType)
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
  loadQuantities2(eventTypes: EventType[], events: Event[], guessType) {
    //TODO: implement
    console.log('(loadQuantities) eventTypes: ' + JSON.stringify(eventTypes) + '; events: ' + JSON.stringify(events) + '; guessType: ' + guessType);
  }

  start() {
    this.stateService.setStartParameters(
      new StartParameters(
        this.selectedQuestionSets.map(s => s.id),
        this.selectedQuantity,
        this.selectedGuessType))
      .subscribe(data => {
        this.router.navigateByUrl('/guess/name');
      });
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
