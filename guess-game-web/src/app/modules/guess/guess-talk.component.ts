import { Component } from '@angular/core';
import { Router } from "@angular/router";
import { SpeakerTalks } from "../../shared/models/speaker-talks.model";
import { StateService } from "../../shared/services/state.service";
import { AnswerService } from "../../shared/services/answer.service";
import { State } from "../../shared/models/state.model";

@Component({
  selector: 'app-guess-talk',
  templateUrl: './guess-talk.component.html'
})
export class GuessTalkComponent {
  private imageDirectory: string = 'assets/images';
  public speakerTalks: SpeakerTalks = new SpeakerTalks();
  public title: string;
  public logoImageSource: string;
  public imageSource: string;

  constructor(private stateService: StateService, private answerService: AnswerService, private router: Router) {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getSpeakerTalks()
      .subscribe(data => {
          if (data) {
            this.speakerTalks = data;
            this.title = `${this.speakerTalks.questionSetName} (${this.speakerTalks.currentIndex + 1}/${this.speakerTalks.totalNumber})`;
            //TODO: implement
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.speakerTalks.currentIndex, id)
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
