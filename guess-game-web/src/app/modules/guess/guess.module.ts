import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GuessWordComponent } from "./guess-word.component";
import { GuessPictureComponent } from "./guess-picture.component";

@NgModule({
  declarations: [
    GuessPictureComponent,
    GuessWordComponent
  ],
  imports: [
    CommonModule
  ]
})
export class GuessModule {
}
