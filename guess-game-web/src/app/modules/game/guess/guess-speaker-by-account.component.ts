import { Component, OnInit } from '@angular/core';
import { AccountSpeakers } from '../../../shared/models/guess/account-speakers.model';
import { StateService } from '../../../shared/services/state.service';
import { AnswerService } from '../../../shared/services/answer.service';
import { Router } from '@angular/router';
import { GameState } from '../../../shared/models/game-state.model';

@Component({
  selector: 'app-guess-speaker-by-account',
  templateUrl: './guess-speaker-by-account.component.html'
})
export class GuessSpeakerByAccountComponent implements OnInit {
  private imageDirectory = 'assets/images';
  private eventsImageDirectory = `${this.imageDirectory}/events`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;
  public accountSpeakers: AccountSpeakers = new AccountSpeakers();
  public title: string;
  public logoImageSource: string;
  public imageSource0: string;
  public imageSource1: string;
  public imageSource2: string;
  public imageSource3: string;

  constructor(private stateService: StateService, private answerService: AnswerService, private router: Router) {
  }

  ngOnInit(): void {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getAccountSpeakers()
      .subscribe(data => {
          if (data) {
            this.accountSpeakers = data;
            this.title =
              `${this.accountSpeakers.questionSetName} (${this.accountSpeakers.currentIndex + 1}/${this.accountSpeakers.totalNumber})`;
            this.imageSource0 = `${this.speakersImageDirectory}/${this.accountSpeakers.speakerPhotoFileName0}`;
            this.imageSource1 = `${this.speakersImageDirectory}/${this.accountSpeakers.speakerPhotoFileName1}`;
            this.imageSource2 = `${this.speakersImageDirectory}/${this.accountSpeakers.speakerPhotoFileName2}`;
            this.imageSource3 = `${this.speakersImageDirectory}/${this.accountSpeakers.speakerPhotoFileName3}`;

            if (this.accountSpeakers.logoFileName) {
              this.logoImageSource = `${this.eventsImageDirectory}/${this.accountSpeakers.logoFileName}`;
            }
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.accountSpeakers.currentIndex, id)
      .subscribe(data => {
          this.loadQuestion();
        }
      );
  }

  result() {
    this.stateService.setState(GameState.ResultState)
      .subscribe(data => {
          this.router.navigateByUrl('/game/result');
        }
      );
  }

  cancel() {
    this.router.navigateByUrl('/game/cancel');
  }
}
