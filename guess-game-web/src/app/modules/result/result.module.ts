import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { ResultComponent } from './result.component';
import { GeneralModule } from '../general/general.module';
import { MessageModule } from '../message/message.module';

@NgModule({
  declarations: [
    ResultComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    TranslateModule,
    GeneralModule,
    MessageModule
  ]
})
export class ResultModule {
}
