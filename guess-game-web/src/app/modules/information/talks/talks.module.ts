import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { YouTubePlayerModule } from '@angular/youtube-player';
import { TranslateModule } from '@ngx-translate/core';
import { MarkdownModule } from 'ngx-markdown';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { TabMenuModule } from 'primeng/tabmenu';
import { GeneralModule } from '../../general/general.module';
import { InformationModule } from '../information.module';
import { MessageModule } from '../../message/message.module';
import { TalkComponent } from './talk.component';
import { TalksSearchComponent } from './talks-search.component';
import { TalksTabMenuComponent } from './talks-tabmenu.component';

@NgModule({
    declarations: [
        TalkComponent,
        TalksSearchComponent,
        TalksTabMenuComponent
    ],
    imports: [
        CommonModule,
        DropdownModule,
        FormsModule,
        InputTextModule,
        MarkdownModule,
        RouterModule,
        TableModule,
        YouTubePlayerModule,
        GeneralModule,
        InformationModule,
        MessageModule,
        TabMenuModule,
        TranslateModule
    ]
})
export class TalksModule {
}
