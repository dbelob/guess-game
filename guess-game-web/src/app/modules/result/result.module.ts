import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResultComponent } from "./result.component";
import { GeneralModule } from "../general/general.module";
import { MessageModule } from "../message/message.module";

@NgModule({
  declarations: [
    ResultComponent
  ],
  imports: [
    CommonModule,
    GeneralModule,
    MessageModule
  ]
})
export class ResultModule {
}
