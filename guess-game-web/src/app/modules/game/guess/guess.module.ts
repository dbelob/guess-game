import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { GuessNameByPhotoComponent } from './guess-name-by-photo.component';
import { GuessPhotoByNameComponent } from './guess-photo-by-name.component';
import { GuessTalkBySpeakerComponent } from './guess-talk-by-speaker.component';
import { GuessSpeakerByTalkComponent } from './guess-speaker-by-talk.component';
import { GuessCompanyBySpeakerComponent } from './guess-company-by-speaker.component';
import { GuessSpeakerByCompanyComponent } from './guess-speaker-by-company.component';
import { GuessAccountBySpeakerComponent } from './guess-account-by-speaker.component';
import { GuessSpeakerByAccountComponent } from './guess-speaker-by-account.component';
import { GuessTagCloudBySpeakerComponent } from './guess-tag-cloud-by-speaker.component';
import { GuessSpeakerByTagCloudComponent } from './guess-speaker-by-tag-cloud.component';
import { CancelGameComponent } from './cancel-game.component';
import { GeneralModule } from '../../general/general.module';
import { MessageModule } from '../../message/message.module';

@NgModule({
  declarations: [
    CancelGameComponent,
    GuessNameByPhotoComponent,
    GuessPhotoByNameComponent,
    GuessTalkBySpeakerComponent,
    GuessSpeakerByTalkComponent,
    GuessCompanyBySpeakerComponent,
    GuessSpeakerByCompanyComponent,
    GuessAccountBySpeakerComponent,
    GuessSpeakerByAccountComponent,
    GuessTagCloudBySpeakerComponent,
    GuessSpeakerByTagCloudComponent
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
