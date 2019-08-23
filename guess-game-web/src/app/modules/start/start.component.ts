import { Component } from '@angular/core';
import { Router } from "@angular/router";
import { QuestionSet } from "../../shared/models/question-set.model";
import { QuestionService } from "../../shared/services/question.service";
import { StateService } from "../../shared/services/state.service";
import { StartParameters } from "../../shared/models/start-parameters.model";

@Component({
  selector: 'app-start',
  templateUrl: './start.component.html',
  styleUrls: ['./start.component.css']
})
export class StartComponent {
  public questionSets: QuestionSet[] = [];
  public quantities: number[] = [];
  public selectedQuestionSets: QuestionSet[] = [];
  public selectedQuantity: number;
  public selectedGuessType: string = 'guessName';

  constructor(private questionService: QuestionService, private stateService: StateService, private router: Router) {
    questionService.getQuestionSets()
      .subscribe(data => {
        this.questionSets = data;

        if (this.questionSets.length > 0) {
          this.selectedQuestionSets = [this.questionSets[0]];
          this.loadQuantities(this.selectedQuestionSets);
        }
      });
  }

  onChange(questionSets: QuestionSet[]) {
    this.loadQuantities(questionSets);
  }

  loadQuantities(questionSets: QuestionSet[]) {
    this.questionService.getQuantities(questionSets.map(s => s.id))
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

  isDisabled(): boolean {
    return this.selectedQuestionSets && (this.selectedQuestionSets.length <= 0);
  }
}
