import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from "@angular/router";
import { TranslateModule } from "@ngx-translate/core";
import { GuessNameByPhotoComponent } from "./guess-name-by-photo.component";
import { GuessPhotoByNameComponent } from "./guess-photo-by-name.component";
import { GuessTalkBySpeakerComponent } from "./guess-talk-by-speaker.component";
import { GuessSpeakerByTalkComponent } from "./guess-speaker-by-talk.component";
import { CancelGameComponent } from "./cancel-game.component";
import { GeneralModule } from "../general/general.module";
import { MessageModule } from "../message/message.module";

@NgModule({
  declarations: [
    CancelGameComponent,
    GuessNameByPhotoComponent,
    GuessPhotoByNameComponent,
    GuessTalkBySpeakerComponent,
    GuessSpeakerByTalkComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    TranslateModule,
    GeneralModule,
    MessageModule
  ]
})
export class GuessModule {
}
