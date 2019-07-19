import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GuessNameComponent } from "./guess-name.component";
import { GuessPictureComponent } from "./guess-picture.component";
import { MessageModule } from "../message/message.module";

@NgModule({
  declarations: [
    GuessPictureComponent,
    GuessNameComponent
  ],
  imports: [
    CommonModule,
    MessageModule
  ]
})
export class GuessModule {
}
