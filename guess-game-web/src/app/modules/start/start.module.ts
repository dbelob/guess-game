import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from "@angular/forms";
import { TranslateModule } from "@ngx-translate/core";
import { StartComponent } from "./start.component";
import { GeneralModule } from "../general/general.module";
import { MessageModule } from "../message/message.module";

@NgModule({
  declarations: [
    StartComponent
  ],
  imports: [
    CommonModule,
    TranslateModule,
    FormsModule,
    GeneralModule,
    MessageModule
  ]
})
export class StartModule {
}
