import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CheckboxModule } from 'primeng/checkbox';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { TranslateModule } from '@ngx-translate/core';
import { GeneralModule } from '../general/general.module';
import { MessageModule } from '../message/message.module';
import { EventTypeStatisticsComponent } from './event-type-statistics.component';

@NgModule({
  declarations: [
    EventTypeStatisticsComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    CheckboxModule,
    TranslateModule,
    TableModule,
    TooltipModule,
    GeneralModule,
    MessageModule
  ]
})
export class StatisticsModule {
}
