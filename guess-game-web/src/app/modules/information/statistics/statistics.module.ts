import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { RouterModule } from '@angular/router';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { TranslateModule } from '@ngx-translate/core';
import { GeneralModule } from '../../general/general.module';
import { InformationModule } from '../information.module';
import { MessageModule } from '../../message/message.module';
import { EventTypeStatisticsComponent } from './event-type-statistics.component';
import { EventStatisticsComponent } from './event-statistics.component';
import { SpeakerStatisticsComponent } from './speaker-statistics.component';
import { CompanyStatisticsComponent } from './company-statistics.component';
import { StatisticsSwitcherComponent } from './statistics-switcher.component';

@NgModule({
  declarations: [
    EventTypeStatisticsComponent,
    EventStatisticsComponent,
    SpeakerStatisticsComponent,
    CompanyStatisticsComponent,
    StatisticsSwitcherComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    CheckboxModule,
    DropdownModule,
    RouterModule,
    TableModule,
    TooltipModule,
    GeneralModule,
    InformationModule,
    MessageModule,
    TranslateModule
  ]
})
export class StatisticsModule {
}
