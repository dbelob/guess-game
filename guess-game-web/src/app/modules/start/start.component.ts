import { Component } from '@angular/core';
import { QuestionSet } from "../../shared/models/question-set.model";
import { QuestionService } from "../../shared/services/question.service";

@Component({
  selector: 'app-start',
  templateUrl: './start.component.html'
})
export class StartComponent {
  protected questionSets: QuestionSet[] = [];

  constructor(private questionService: QuestionService) {
    questionService.getQuestionSets().subscribe(data => {
      this.questionSets = data;
    });
  }

  onQuestionSetChange(questionSetValue) {
    console.log('questionSetValue: ' + questionSetValue);
  }

  start() {
    //TODO: implement
  }
}
