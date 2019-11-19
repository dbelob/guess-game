import { Component } from '@angular/core';
import { Router } from "@angular/router";
import { PictureNames } from "../../shared/models/picture-names.model";
import { StateService } from "../../shared/services/state.service";
import { AnswerService } from "../../shared/services/answer.service";
import { State } from "../../shared/models/state.model";

@Component({
  selector: 'app-guess-name',
  templateUrl: './guess-name.component.html'
})
export class GuessNameComponent {
  private imageDirectory: string = 'assets/images';
  private eventsImageDirectory: string = `${this.imageDirectory}/events`;
  private speakersImageDirectory: string = `${this.imageDirectory}/speakers`;
  public pictureNames: PictureNames = new PictureNames();
  public title: string;
  public logoImageSource: string;
  public imageSource: string;

  constructor(private stateService: StateService, private answerService: AnswerService, private router: Router) {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getPictureNames()
      .subscribe(data => {
          if (data) {
            this.pictureNames = data;
            this.title = `${this.pictureNames.questionSetName} (${this.pictureNames.currentIndex + 1}/${this.pictureNames.totalNumber})`;
            this.imageSource = `${this.speakersImageDirectory}/${this.pictureNames.fileName}`;

            if (this.pictureNames.logoFileName) {
              this.logoImageSource = `${this.eventsImageDirectory}/${this.pictureNames.logoFileName}`;
            }
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.pictureNames.currentIndex, id)
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
