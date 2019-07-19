import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResultComponent } from "./result.component";
import { MessageModule } from "../message/message.module";

@NgModule({
  declarations: [
    ResultComponent
  ],
  imports: [
    CommonModule,
    MessageModule
  ]
})
export class ResultModule {
}
