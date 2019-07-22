import { Component } from '@angular/core';
import { MessageService } from "../message/message.service";
import { Result } from "../../shared/models/result.model";
import { AnswerService } from "../../shared/services/answer.service";

@Component({
  selector: 'app-result',
  templateUrl: './result.component.html'
})
export class ResultComponent {
  private baseUrl = 'api/answer';
  private result = new Result();

  constructor(private answerService: AnswerService, private messageService: MessageService) {
    answerService.getResult()
      .subscribe(data => {
        this.result = data;
      })
  }

  restart() {
    //TODO: implement
  }
}
