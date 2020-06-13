import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';
import { registerLocaleData } from '@angular/common';
import localeRu from '@angular/common/locales/ru';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';

import { AppComponent } from './app.component';
import { HomeModule } from './modules/home/home.module';
import { StartModule } from './modules/start/start.module';
import { GuessModule } from './modules/guess/guess.module';
import { MessageModule } from './modules/message/message.module';
import { ResultModule } from './modules/result/result.module';
import { StatisticsModule } from './modules/statistics/statistics.module';
import { UnknownModule } from './modules/unknown/unknown.module';
import { HomeComponent } from './modules/home/home.component';
import { StartComponent } from './modules/start/start.component';
import { ResultComponent } from './modules/result/result.component';
import { GuessNameByPhotoComponent } from './modules/guess/guess-name-by-photo.component';
import { GuessPhotoByNameComponent } from './modules/guess/guess-photo-by-name.component';
import { GuessTalkBySpeakerComponent } from './modules/guess/guess-talk-by-speaker.component';
import { GuessSpeakerByTalkComponent } from './modules/guess/guess-speaker-by-talk.component';
import { GuessAccountBySpeakerComponent } from './modules/guess/guess-account-by-speaker.component';
import { GuessSpeakerByAccountComponent } from './modules/guess/guess-speaker-by-account.component';
import { CancelGameComponent } from './modules/guess/cancel-game.component';
import { NotFoundComponent } from './modules/unknown/not-found.component';
import { EventTypeStatisticsComponent } from './modules/statistics/event-type-statistics.component';
import { EventStatisticsComponent } from './modules/statistics/event-statistics.component';
import { SpeakerStatisticsComponent } from './modules/statistics/speaker-statistics.component';
import { AnswerService } from './shared/services/answer.service';
import { QuestionService } from './shared/services/question.service';
import { StateService } from './shared/services/state.service';
import { StatisticsService } from './shared/services/statistics.service';
import { StateGuard } from './shared/guards/state.guard';

const routes: Routes = [
  {path: 'home', component: HomeComponent},
  {path: 'start', component: StartComponent, canActivate: [StateGuard]},
  {path: 'guess/name-by-photo', component: GuessNameByPhotoComponent, canActivate: [StateGuard]},
  {path: 'guess/photo-by-name', component: GuessPhotoByNameComponent, canActivate: [StateGuard]},
  {path: 'guess/talk-by-speaker', component: GuessTalkBySpeakerComponent, canActivate: [StateGuard]},
  {path: 'guess/speaker-by-talk', component: GuessSpeakerByTalkComponent, canActivate: [StateGuard]},
  {path: 'guess/account-by-speaker', component: GuessAccountBySpeakerComponent, canActivate: [StateGuard]},
  {path: 'guess/speaker-by-account', component: GuessSpeakerByAccountComponent, canActivate: [StateGuard]},
  {path: 'result', component: ResultComponent, canActivate: [StateGuard]},
  {path: 'cancel', component: CancelGameComponent},
  {path: 'information/statistics/event-types', component: EventTypeStatisticsComponent},
  {path: 'information/statistics/events', component: EventStatisticsComponent},
  {path: 'information/statistics/speakers', component: SpeakerStatisticsComponent},
  {path: 'information', redirectTo: 'information/statistics/event-types'},
  {path: '', pathMatch: 'full', redirectTo: 'home'},
  {path: '**', component: NotFoundComponent}
];

// AoT requires an exported function for factories
export function HttpLoaderFactory(httpClient: HttpClient) {
  return new TranslateHttpLoader(httpClient);
}

registerLocaleData(localeRu, 'ru');

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    HomeModule,
    GuessModule,
    MessageModule,
    ResultModule,
    StartModule,
    StatisticsModule,
    UnknownModule
  ],
  providers: [AnswerService, QuestionService, StateService, StatisticsService, StateGuard],
  bootstrap: [AppComponent]
})
export class AppModule {
}
