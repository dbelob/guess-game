import { Component, OnInit } from '@angular/core';
import { SpeakerAccounts } from "../../shared/models/speaker-accounts.model";
import { StateService } from "../../shared/services/state.service";
import { State } from "../../shared/models/state.model";
import { AnswerService } from "../../shared/services/answer.service";
import { Router } from "@angular/router";

@Component({
  selector: 'app-guess-accounts-by-speaker',
  templateUrl: './guess-accounts-by-speaker.component.html'
})
export class GuessAccountsBySpeakerComponent implements OnInit {
  private imageDirectory: string = 'assets/images';
  private eventsImageDirectory: string = `${this.imageDirectory}/events`;
  public speakersImageDirectory: string = `${this.imageDirectory}/speakers`;
  public speakerAccounts: SpeakerAccounts = new SpeakerAccounts();
  public title: string;
  public logoImageSource: string;

  constructor(private stateService: StateService, private answerService: AnswerService, private router: Router) {
  }

  ngOnInit(): void {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getSpeakerAccounts()
      .subscribe(data => {
          if (data) {
            console.log('speakerAccounts: ' + JSON.stringify(data));
            this.speakerAccounts = data;
            this.title = `${this.speakerAccounts.questionSetName} (${this.speakerAccounts.currentIndex + 1}/${this.speakerAccounts.totalNumber})`;

            if (this.speakerAccounts.logoFileName) {
              this.logoImageSource = `${this.eventsImageDirectory}/${this.speakerAccounts.logoFileName}`;
            }
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.speakerAccounts.currentIndex, id)
      .subscribe(data => {
          this.loadQuestion();
        }
      );
  }

  result() {
    this.stateService.setState(State.ResultState)
      .subscribe(data => {
          this.router.navigateByUrl('/result');
        }
      );
  }

  cancel() {
    this.router.navigateByUrl('/cancel');
  }
}
