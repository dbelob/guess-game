import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GuessNameComponent } from "./guess-name.component";
import { GuessPictureComponent } from "./guess-picture.component";

@NgModule({
  declarations: [
    GuessPictureComponent,
    GuessNameComponent
  ],
  imports: [
    CommonModule
  ]
})
export class GuessModule {
}
