import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Result } from '../../../shared/models/result/result.model';
import { GuessMode } from '../../../shared/models/guess-mode.model';
import { AnswerService } from '../../../shared/services/answer.service';
import { StateService } from '../../../shared/services/state.service';

@Component({
  selector: 'app-result',
  templateUrl: './result.component.html'
})
export class ResultComponent implements OnInit {
  public speakersImageDirectory = 'assets/images/speakers';
  public imageSourcePrefix = 'data:image/jpeg;base64,';
  public result = new Result();
  public isQuestionImage = true;

  constructor(private answerService: AnswerService, private stateService: StateService, private router: Router,
              public translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.loadResult();
  }

  loadResult() {
    this.answerService.getResult()
      .subscribe(data => {
        this.result = data;
        this.isQuestionImage = (GuessMode.GuessNameByPhotoMode === this.result.guessMode) ||
          (GuessMode.GuessTalkBySpeakerMode === this.result.guessMode) ||
          (GuessMode.GuessCompanyBySpeakerMode === this.result.guessMode) ||
          (GuessMode.GuessAccountBySpeakerMode === this.result.guessMode) ||
          (GuessMode.GuessTagCloudBySpeakerMode === this.result.guessMode);
      });
  }

  home() {
    this.stateService.deleteStartParameters()
      .subscribe(data => {
          this.router.navigateByUrl('/home');
        }
      );
  }

  restart() {
    this.stateService.deleteStartParameters()
      .subscribe(data => {
          this.router.navigateByUrl('/game/start');
        }
      );
  }

  isSkippedVisible() {
    return (this.result.skippedAnswers > 0);
  }

  isSpeakerErrorDetailsListVisible() {
    return (this.result.speakerErrorDetailsList && (this.result.speakerErrorDetailsList.length > 0));
  }

  isTalkErrorDetailsListVisible() {
    return (this.result.talkErrorDetailsList && (this.result.talkErrorDetailsList.length > 0));
  }

  isCompanyErrorDetailsListVisible() {
    return (this.result.companyErrorDetailsList && (this.result.companyErrorDetailsList.length > 0));
  }

  isAccountErrorDetailsListVisible() {
    return (this.result.accountErrorDetailsList && (this.result.accountErrorDetailsList.length > 0));
  }

  isTagCloudErrorDetailsListVisible() {
    return (this.result.tagCloudErrorDetailsList && (this.result.tagCloudErrorDetailsList.length > 0));
  }

  isErrorDetailsListVisible() {
    return (this.isSpeakerErrorDetailsListVisible() || this.isTalkErrorDetailsListVisible() ||
      this.isCompanyErrorDetailsListVisible() || this.isAccountErrorDetailsListVisible() ||
      this.isTagCloudErrorDetailsListVisible());
  }
}
