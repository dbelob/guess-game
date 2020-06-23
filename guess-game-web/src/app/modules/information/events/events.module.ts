import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { GeneralModule } from '../../general/general.module';
import { InformationModule } from '../information.module';
import { MessageModule } from '../../message/message.module';
import { EventsSearchComponent } from './events-search.component';
import { EventsSwitcherComponent } from './events-switcher.component';

@NgModule({
  declarations: [
    EventsSearchComponent,
    EventsSwitcherComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    GeneralModule,
    InformationModule,
    MessageModule,
    TranslateModule
  ]
})
export class EventsModule {
}
