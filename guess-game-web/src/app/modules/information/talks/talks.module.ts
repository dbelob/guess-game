import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { MarkdownModule } from 'ngx-markdown';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { GeneralModule } from '../../general/general.module';
import { InformationModule } from '../information.module';
import { MessageModule } from '../../message/message.module';
import { TalkComponent } from './talk.component';
import { TalksSearchComponent } from './talks-search.component';
import { TalksSwitcherComponent } from './talks-switcher.component';

@NgModule({
  declarations: [
    TalkComponent,
    TalksSearchComponent,
    TalksSwitcherComponent
  ],
  imports: [
    CommonModule,
    DropdownModule,
    FormsModule,
    InputTextModule,
    MarkdownModule,
    RouterModule,
    TableModule,
    GeneralModule,
    InformationModule,
    MessageModule,
    TranslateModule
  ]
})
export class TalksModule {
}
