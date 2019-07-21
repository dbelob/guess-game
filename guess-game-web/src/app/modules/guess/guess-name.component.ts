import { Component } from '@angular/core';
import { StateService } from "../../shared/services/state.service";
import { PictureNames } from "../../shared/models/picture-names.model";

@Component({
  selector: 'app-guess-name',
  templateUrl: './guess-name.component.html'
})
export class GuessNameComponent {
  private imageDirectory: string = 'assets/images';
  private pictureNames: PictureNames = new PictureNames();
  private title: string;

  constructor(private stateService: StateService) {
    stateService.getPictureNames().subscribe(data => {
        this.pictureNames = data;
        this.title = `${this.pictureNames.questionSetName} (${this.pictureNames.currentNumber}/${this.pictureNames.totalNumber})`;
      }
    );
  }

  answer() {
    //TODO: implement
  }
}
