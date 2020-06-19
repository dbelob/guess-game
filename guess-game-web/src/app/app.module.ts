import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';
import { registerLocaleData } from '@angular/common';
import localeRu from '@angular/common/locales/ru';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MarkdownModule } from 'ngx-markdown';

import { AppComponent } from './app.component';
import { HomeModule } from './modules/home/home.module';
import { InformationModule } from './modules/information/information.module';
import { GuessModule } from './modules/game/guess/guess.module';
import { MessageModule } from './modules/message/message.module';
import { ResultModule } from './modules/game/result/result.module';
import { StartModule } from './modules/game/start/start.module';
import { SpeakersModule } from './modules/information/speakers/speakers.module';
import { StatisticsModule } from './modules/information/statistics/statistics.module';
import { TalksModule } from './modules/information/talks/talks.module';
import { UnknownModule } from './modules/unknown/unknown.module';
import { HomeComponent } from './modules/home/home.component';
import { StartComponent } from './modules/game/start/start.component';
import { ResultComponent } from './modules/game/result/result.component';
import { GuessNameByPhotoComponent } from './modules/game/guess/guess-name-by-photo.component';
import { GuessPhotoByNameComponent } from './modules/game/guess/guess-photo-by-name.component';
import { GuessTalkBySpeakerComponent } from './modules/game/guess/guess-talk-by-speaker.component';
import { GuessSpeakerByTalkComponent } from './modules/game/guess/guess-speaker-by-talk.component';
import { GuessAccountBySpeakerComponent } from './modules/game/guess/guess-account-by-speaker.component';
import { GuessSpeakerByAccountComponent } from './modules/game/guess/guess-speaker-by-account.component';
import { CancelGameComponent } from './modules/game/guess/cancel-game.component';
import { NotFoundComponent } from './modules/unknown/not-found.component';
import { TalksSearchComponent } from './modules/information/talks/talks-search.component';
import { SpeakerComponent } from './modules/information/speakers/speaker.component';
import { SpeakersListComponent } from './modules/information/speakers/speakers-list.component';
import { SpeakersSearchComponent } from './modules/information/speakers/speakers-search.component';
import { EventTypeStatisticsComponent } from './modules/information/statistics/event-type-statistics.component';
import { EventStatisticsComponent } from './modules/information/statistics/event-statistics.component';
import { SpeakerStatisticsComponent } from './modules/information/statistics/speaker-statistics.component';
import { AnswerService } from './shared/services/answer.service';
import { EventTypeService } from './shared/services/event-type.service';
import { QuestionService } from './shared/services/question.service';
import { StateService } from './shared/services/state.service';
import { SpeakerService } from './shared/services/speaker.service';
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
  {path: 'information/talks/search', component: TalksSearchComponent},
  {path: 'information/speaker/:id', component: SpeakerComponent},
  {path: 'information/speakers/list', component: SpeakersListComponent},
  {path: 'information/speakers/search', component: SpeakersSearchComponent},
  {path: 'information/statistics/event-types', component: EventTypeStatisticsComponent},
  {path: 'information/statistics/events', component: EventStatisticsComponent},
  {path: 'information/statistics/speakers', component: SpeakerStatisticsComponent},
  {path: 'information/talks', redirectTo: 'information/talks/search'},
  {path: 'information/speakers', redirectTo: 'information/speakers/list'},
  {path: 'information/statistics', redirectTo: 'information/statistics/event-types'},
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
    MarkdownModule.forRoot({
      loader: HttpClient
    }),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    HomeModule,
    InformationModule,
    GuessModule,
    MessageModule,
    ResultModule,
    StartModule,
    SpeakersModule,
    StatisticsModule,
    TalksModule,
    UnknownModule
  ],
  providers: [
    AnswerService,
    EventTypeService,
    QuestionService,
    StateService,
    SpeakerService,
    StatisticsService,
    StateGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
