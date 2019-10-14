import { Component } from '@angular/core';
import { Router } from "@angular/router";
import { TalkSpeakers } from "../../shared/models/talk-speakers.model";
import { StateService } from "../../shared/services/state.service";
import { AnswerService } from "../../shared/services/answer.service";
import { State } from "../../shared/models/state.model";

@Component({
  selector: 'app-guess-speaker',
  templateUrl: './guess-speaker.component.html'
})
export class GuessSpeakerComponent {
  private imageDirectory: string = 'assets/images';
  public talkSpeakers: TalkSpeakers = new TalkSpeakers();
  public title: string;
  public logoImageSource: string;
  public imageSource0: string;
  public imageSource1: string;
  public imageSource2: string;
  public imageSource3: string;

  constructor(private stateService: StateService, private answerService: AnswerService, private router: Router) {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getTalkSpeakers()
      .subscribe(data => {
          if (data) {
            this.talkSpeakers = data;
            this.title = `${this.talkSpeakers.questionSetName} (${this.talkSpeakers.currentIndex + 1}/${this.talkSpeakers.totalNumber})`;
            //TODO: implement
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.talkSpeakers.currentIndex, id)
      .subscribe(data => {
          this.loadQuestion();
        }
      );
  }

  result() {
    this.stateService.setState(State.ResultState)
      .subscribe(date => {
          this.router.navigateByUrl('/result');
        }
      );
  }

  cancel() {
    this.router.navigateByUrl('/cancel');
  }
}
