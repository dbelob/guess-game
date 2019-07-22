import { Component } from '@angular/core';
import { StateService } from "../../shared/services/state.service";
import { PictureNames } from "../../shared/models/picture-names.model";
import { State } from "../../shared/models/state.model";
import { Router } from "@angular/router";

@Component({
  selector: 'app-guess-name',
  templateUrl: './guess-name.component.html'
})
export class GuessNameComponent {
  private imageDirectory: string = 'assets/images';
  private pictureNames: PictureNames = new PictureNames();
  private title: string;
  private imageSource: string;

  constructor(private stateService: StateService, private router: Router) {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getPictureNames().subscribe(data => {
        if (data) {
          this.pictureNames = data;
          this.title = `${this.pictureNames.questionSetName} (${this.pictureNames.currentNumber}/${this.pictureNames.totalNumber})`;
          this.imageSource = `${this.imageDirectory}/${this.pictureNames.fileName}`;
        } else {
          this.result();
        }
      }
    );
  }

  answer(id: number) {
    //TODO: implement
    console.log("answer(" + this.pictureNames.currentNumber + ', ' + id + ')');
  }

  result() {
    this.stateService.setState(State.ResultState).subscribe(date => {
        this.router.navigateByUrl('/result');
      }
    );
  }
}
