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
  public questionSets: QuestionSet[] = [];
  public selectedQuestionSets: QuestionSet[] = [];

  public eventTypes: EventType[];
  public selectedEventTypes: EventType[] = [];

  public events: Event[];
  public selectedEvents = [];

  public guessType = GuessType;
  public selectedGuessType: GuessType = GuessType.GuessNameType;

  public quantities: number[] = [];
  public selectedQuantity: number;

  constructor(private questionService: QuestionService, private stateService: StateService, private router: Router,
              public translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.loadQuestionSets();
    this.loadEventTypes();
  }

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

  onSetChange(questionSets: QuestionSet[]) {
    this.loadQuantities(questionSets, this.selectedGuessType);
  }

  loadEventTypes() {
    this.questionService.getEventTypes()
      .subscribe(data => {
        this.eventTypes = data;

        if (this.eventTypes.length > 0) {
          //TODO: change
          this.selectedEventTypes = [this.eventTypes[0]];
          this.loadEvent(this.selectedEventTypes);
        }
      });
  }

  onEventTypeChange(eventTypes: EventType[]) {
    this.loadEvent(eventTypes);
  }

  loadEvent(eventTypes: EventType[]) {
    if ((eventTypes.length == 1) && eventTypes[0].conference) {
      this.questionService.getEvents(eventTypes[0].id)
        .subscribe(data => {
          this.events = data;

          if (this.events.length > 0) {
            this.selectedEvents = [this.events[this.events.length - 1]];
          }
        });
    } else {
      this.events = [];
      this.selectedEvents = [];
    }
  }

  onEventChange(events: Event[]) {
    //TODO: implements
  }

  onTypeChange(guessType: string) {
    this.loadQuantities(this.selectedQuestionSets, guessType);
  }

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
}
