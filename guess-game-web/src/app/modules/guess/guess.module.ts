import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from "@angular/router";
import { GuessNameComponent } from "./guess-name.component";
import { GuessPictureComponent } from "./guess-picture.component";
import { CancelGameComponent } from "./cancel-game.component";
import { MessageModule } from "../message/message.module";

@NgModule({
  declarations: [
    CancelGameComponent,
    GuessPictureComponent,
    GuessNameComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    MessageModule
  ]
})
export class GuessModule {
}
