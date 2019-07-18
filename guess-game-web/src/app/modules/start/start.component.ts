import { Component } from '@angular/core';
import { QuestionSet } from "../../shared/models/question-set.model";
import { QuestionService } from "../../shared/services/question.service";

@Component({
  selector: 'app-start',
  templateUrl: './start.component.html'
})
export class StartComponent {
  private questionSets: QuestionSet[] = [];
  private quantities: number[] = [];
  private selectedQuestionSet: QuestionSet;
  private selectedQuantity: number;
  private selectedType: string = 'picture';

  constructor(private questionService: QuestionService) {
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
      '; selectedType: ' + JSON.stringify(this.selectedType));
  }
}
