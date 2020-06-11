import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { DropdownModule } from 'primeng/dropdown';
import { ListboxModule } from 'primeng/listbox';
import { RadioButtonModule } from 'primeng/radiobutton';
import { StartComponent } from './start.component';
import { GeneralModule } from '../general/general.module';
import { MessageModule } from '../message/message.module';

@NgModule({
  declarations: [
    StartComponent
  ],
  imports: [
    CommonModule,
    DropdownModule,
    FormsModule,
    ListboxModule,
    RadioButtonModule,
    TranslateModule,
    GeneralModule,
    MessageModule
  ]
})
export class StartModule {
}
