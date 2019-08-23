import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StartComponent } from "./start.component";
import { FormsModule } from "@angular/forms";
import { MessageModule } from "../message/message.module";
import { CommonInfoModule } from '../common-info/common-info.module';
import { MatFormFieldModule, MatSelectModule, MatRadioModule } from '@angular/material';

@NgModule({
  declarations: [
    StartComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    MessageModule,
    MessageModule,
    CommonInfoModule,
    MatSelectModule,
    MatFormFieldModule,
    MatRadioModule
  ]
})
export class StartModule {
}
