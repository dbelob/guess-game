import { Component } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";
import { MessageService } from "../message/message.service";
import { Result } from "../../shared/models/result.model";
import { AnswerService } from "../../shared/services/answer.service";
import { StateService } from "../../shared/services/state.service";
import { State } from "../../shared/models/state.model";
import { Router } from "@angular/router";
import { GuessType } from "../../shared/models/guess-type.model";

@Component({
  selector: 'app-result',
  templateUrl: './result.component.html'
})
export class ResultComponent {
  private speakersImageDirectory: string = 'assets/images/speakers';
  public result = new Result();
  private isQuestionPicture = true;

  constructor(private answerService: AnswerService, private stateService: StateService, private router: Router,
              private messageService: MessageService, public translateService: TranslateService) {
    this.loadResult();
  }

  loadResult() {
    this.answerService.getResult()
      .subscribe(data => {
        this.result = data;
        this.isQuestionPicture = (GuessType.GuessNameType === this.result.guessType) || (GuessType.GuessTalkType === this.result.guessType);
      })
  }

  restart() {
    this.stateService.setState(State.StartState)
      .subscribe(date => {
          this.router.navigateByUrl('/start');
        }
      );
  }

  isSkippedVisible() {
    return this.result.skippedAnswers > 0;
  }

  isSpeakerErrorDetailsListVisible() {
    return this.result.speakerErrorDetailsList && (this.result.speakerErrorDetailsList.length > 0);
  }

  isTalkErrorDetailsListVisible() {
    return this.result.talkErrorDetailsList && (this.result.talkErrorDetailsList.length > 0);
  }

  isErrorDetailsListVisible() {
    return this.isSpeakerErrorDetailsListVisible() || this.isTalkErrorDetailsListVisible();
  }
}
