import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { StartModule } from "./modules/start/start.module";
import { GuessModule } from "./modules/guess/guess.module";
import { ResultModule } from "./modules/result/result.module";
import { RouterModule, Routes } from "@angular/router";
import { StartComponent } from "./modules/start/start.component";
import { ResultComponent } from "./modules/result/result.component";
import { GuessNameComponent } from "./modules/guess/guess-name.component";
import { GuessPictureComponent } from "./modules/guess/guess-picture.component";
import { UnknownModule } from "./modules/unknown/unknown.module";
import { NotFoundComponent } from "./modules/unknown/not-found.component";
import { MessageModule } from "./modules/message/message.module";
import { HttpClientModule } from "@angular/common/http";
import { AnswerService } from "./shared/services/answer.service";
import { QuestionService } from "./shared/services/question.service";
import { StateService } from "./shared/services/state.service";
import { StateGuard } from "./shared/guards/state.guard";
import { CancelGameComponent } from "./modules/guess/cancel-game.component";
import { CommonInfoModule } from './modules/common-info/common-info.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

const routes: Routes = [
  {path: 'start', component: StartComponent, canActivate: [StateGuard]},
  {path: 'guess/name', component: GuessNameComponent, canActivate: [StateGuard]},
  {path: 'guess/picture', component: GuessPictureComponent, canActivate: [StateGuard]},
  {path: 'result', component: ResultComponent, canActivate: [StateGuard]},
  {path: 'cancel', component: CancelGameComponent},
  {path: '', pathMatch: 'full', redirectTo: 'start'},
  {path: "**", component: NotFoundComponent}
];

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    HttpClientModule,
    GuessModule,
    CommonInfoModule,
    MessageModule,
    ResultModule,
    StartModule,
    UnknownModule,
    BrowserAnimationsModule,
  ],
  providers: [AnswerService, QuestionService, StateService, StateGuard],
  bootstrap: [AppComponent]
})
export class AppModule {
}
