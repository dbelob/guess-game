import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { StartModule } from "./modules/start/start.module";
import { GuessModule } from "./modules/guess/guess.module";
import { ResultModule } from "./modules/result/result.module";
import { RouterModule, Routes } from "@angular/router";
import { StartComponent } from "./modules/start/start.component";
import { ResultComponent } from "./modules/result/result.component";
import { GuessPictureComponent } from "./modules/guess/guess-picture.component";
import { GuessWordComponent } from "./modules/guess/guess-word.component";
import { UnknownModule } from "./modules/unknown/unknown.module";
import { NotFoundComponent } from "./modules/unknown/not-found.component";

const routes: Routes = [
  {path: 'start', component: StartComponent},
  {path: 'guess/picture', component: GuessPictureComponent},
  {path: 'guess/word', component: GuessWordComponent},
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
    GuessModule,
    ResultModule,
    StartModule,
    UnknownModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
