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

  constructor(private stateService: StateService, private router: Router) {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getPictureNames().subscribe(data => {
        if (data) {
          this.pictureNames = data;
          this.title = `${this.pictureNames.questionSetName} (${this.pictureNames.currentNumber}/${this.pictureNames.totalNumber})`;
        } else {
          this.result();
        }
      }
    );
  }

  answer() {
    //TODO: implement
  }

  result() {
    this.stateService.setState(State.ResultState).subscribe(date => {
        this.router.navigateByUrl('/result');
      }
    );
  }
}
