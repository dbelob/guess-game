import { Component } from '@angular/core';
import { Router } from "@angular/router";
import { PictureNames } from "../../shared/models/picture-names.model";
import { State } from "../../shared/models/state.model";
import { StateService } from "../../shared/services/state.service";
import { AnswerService } from "../../shared/services/answer.service";

@Component({
  selector: 'app-guess-name',
  templateUrl: './guess-name.component.html'
})
export class GuessNameComponent {
  private imageDirectory: string = 'assets/images';
  private pictureNames: PictureNames = new PictureNames();
  private title: string;
  private imageSource: string;

  constructor(private stateService: StateService, private answerService: AnswerService, private router: Router) {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getPictureNames()
      .subscribe(data => {
          if (data) {
            this.pictureNames = data;
            this.title = `${this.pictureNames.questionSetName} (${this.pictureNames.currentIndex + 1}/${this.pictureNames.totalNumber})`;
            this.imageSource = `${this.imageDirectory}/${this.pictureNames.fileName}`;
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
      .subscribe(date => {
          this.router.navigateByUrl('/result');
        }
      );
  }

  cancel() {
    this.router.navigateByUrl('/cancel');
  }
}
