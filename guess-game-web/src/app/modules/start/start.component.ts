import { Component } from '@angular/core';
import { Router } from "@angular/router";
import { QuestionSet } from "../../shared/models/question-set.model";
import { QuestionService } from "../../shared/services/question.service";
import { StateService } from "../../shared/services/state.service";
import { StartParameters } from "../../shared/models/start-parameters.model";
import { GuessType } from "../../shared/models/guess-type.model";

@Component({
  selector: 'app-start',
  templateUrl: './start.component.html'
})
export class StartComponent {
  public questionSets: QuestionSet[] = [];
  public quantities: number[] = [];
  public selectedQuestionSets: QuestionSet[] = [];
  public selectedQuantity: number;
  public selectedGuessType: GuessType = GuessType.GuessNameType;
  public guessType = GuessType;

  constructor(private questionService: QuestionService, private stateService: StateService, private router: Router) {
    this.loadQuestionSets();
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

  onTypeChange(guessType: string) {
    this.loadQuantities(this.selectedQuestionSets, guessType);
  }

  loadQuantities(questionSets: QuestionSet[], guessType: string) {
    this.questionService.getQuantities(questionSets.map(s => s.id), guessType)
      .subscribe(data => {
        this.quantities = data;

        if (this.quantities.length > 0) {
          this.selectedQuantity = this.quantities[this.quantities.length - 1];
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
    return this.selectedQuestionSets && (this.selectedQuestionSets.length <= 0);
  }
}
