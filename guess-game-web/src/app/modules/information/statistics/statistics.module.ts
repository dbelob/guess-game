import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { ButtonModule } from 'primeng/button';
import { ChartModule } from 'primeng/chart';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { MultiSelectModule } from 'primeng/multiselect';
import { RippleModule } from 'primeng/ripple';
import { RouterModule } from '@angular/router';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { TranslateModule } from '@ngx-translate/core';
import { GeneralModule } from '../../general/general.module';
import { InformationModule } from '../information.module';
import { MessageModule } from '../../message/message.module';
import { ChartTypeSwitcherComponent } from './chart-type-switcher.component';
import { EventTypeStatisticsComponent } from './event-type-statistics.component';
import { EventStatisticsComponent } from './event-statistics.component';
import { SpeakerStatisticsComponent } from './speaker-statistics.component';
import { CompanyStatisticsComponent } from './company-statistics.component';
import { OlapStatisticsComponent } from './olap-statistics.component';
import { StatisticsTabMenuComponent } from "./statistics-tabmenu.component";

@NgModule({
  declarations: [
    ChartTypeSwitcherComponent,
    EventTypeStatisticsComponent,
    EventStatisticsComponent,
    SpeakerStatisticsComponent,
    CompanyStatisticsComponent,
    OlapStatisticsComponent,
    StatisticsTabMenuComponent
  ],
  imports: [
    AutoCompleteModule,
    ButtonModule,
    ChartModule,
    CommonModule,
    FormsModule,
    CheckboxModule,
    DropdownModule,
    MultiSelectModule,
    RippleModule,
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
