import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { GeneralModule } from '../../general/general.module';
import { InformationModule } from '../information.module';
import { MessageModule } from '../../message/message.module';
import { TalksSearchComponent } from './talks-search.component';
import { TalksSwitcherComponent } from './talks-switcher.component';

@NgModule({
  declarations: [
    TalksSearchComponent,
    TalksSwitcherComponent
  ],
  imports: [
    CommonModule,
    DropdownModule,
    FormsModule,
    InputTextModule,
    RouterModule,
    GeneralModule,
    InformationModule,
    MessageModule,
    TranslateModule
  ]
})
export class TalksModule {
}
