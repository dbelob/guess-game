import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { GeneralModule } from '../general/general.module';
import { MessageModule } from '../message/message.module';
import { NotFoundComponent } from './not-found.component';

@NgModule({
  declarations: [
    NotFoundComponent
  ],
  imports: [
    CommonModule,
    GeneralModule,
    MessageModule,
    RouterModule,
    TranslateModule
  ]
})
export class UnknownModule {
}
