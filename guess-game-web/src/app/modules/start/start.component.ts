import { Component } from '@angular/core';
import { Router } from "@angular/router";
import { QuestionSet } from "../../shared/models/question-set.model";
import { QuestionService } from "../../shared/services/question.service";
import { StateService } from "../../shared/services/state.service";
import { StartParameters } from "../../shared/models/start-parameters.model";

@Component({
  selector: 'app-start',
  templateUrl: './start.component.html'
})
export class StartComponent {
  private questionSets: QuestionSet[] = [];
  private quantities: number[] = [];
  private selectedQuestionSet: QuestionSet;
  private selectedQuantity: number;
  private selectedGuessType: string = 'guessName';

  constructor(private questionService: QuestionService, private stateService: StateService, private router: Router) {
    questionService.getQuestionSets().subscribe(data => {
      this.questionSets = data;

      if (this.questionSets.length > 0) {
        this.selectedQuestionSet = this.questionSets[0];
        this.loadQuantities(this.selectedQuestionSet.id);
      }
    });
  }

  onChange(questionSet: QuestionSet) {
    this.loadQuantities(questionSet.id);
  }

  loadQuantities(id: number) {
    this.questionService.getQuantities(id).subscribe(data => {
      this.quantities = data;

      if (this.quantities.length > 0) {
        this.selectedQuantity = this.quantities[this.quantities.length - 1];
      }
    });
  }

  start() {
    console.log('selectedQuestionSet: ' + JSON.stringify(this.selectedQuestionSet) +
      '; selectedQuantity: ' + JSON.stringify(this.selectedQuantity) +
      '; selectedGuessType: ' + JSON.stringify(this.selectedGuessType));

    this.stateService.setStartParameters(
      new StartParameters(
        this.selectedQuestionSet.id,
        this.selectedQuantity,
        this.selectedGuessType)).subscribe(data => {
      this.router.navigateByUrl('/guess/name');
    });
  }
}
