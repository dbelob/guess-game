import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { MarkdownModule } from 'ngx-markdown';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { GeneralModule } from '../../general/general.module';
import { InformationModule } from '../information.module';
import { MessageModule } from '../../message/message.module';
import { SpeakerComponent } from './speaker.component';
import { SpeakersListComponent } from './speakers-list.component';
import { SpeakersMenubarComponent } from './speakers-menubar.component';
import { SpeakersSearchComponent } from './speakers-search.component';

@NgModule({
  declarations: [
    SpeakerComponent,
    SpeakersListComponent,
    SpeakersMenubarComponent,
    SpeakersSearchComponent
  ],
  imports: [
    AutoCompleteModule,
    CommonModule,
    CheckboxModule,
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
export class SpeakersModule {
}
