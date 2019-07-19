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
import { QuestionService } from "./shared/services/question.service";
import { MessageModule } from "./modules/message/message.module";
import { HttpClientModule } from "@angular/common/http";

const routes: Routes = [
  {path: 'start', component: StartComponent},
  {path: 'guess/name', component: GuessNameComponent},
  {path: 'guess/picture', component: GuessPictureComponent},
  {path: 'result', component: ResultComponent},
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
    MessageModule,
    ResultModule,
    StartModule,
    UnknownModule
  ],
  providers: [QuestionService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
