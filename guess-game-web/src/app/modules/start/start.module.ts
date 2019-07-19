import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StartComponent } from "./start.component";
import { FormsModule } from "@angular/forms";
import { MessageModule } from "../message/message.module";

@NgModule({
  declarations: [
    StartComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    MessageModule
  ]
})
export class StartModule {
}
