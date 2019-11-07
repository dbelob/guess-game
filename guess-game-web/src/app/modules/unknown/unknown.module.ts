import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotFoundComponent } from "./not-found.component";
import { GeneralModule } from "../general/general.module";
import { RouterModule } from "@angular/router";

@NgModule({
  declarations: [
    NotFoundComponent
  ],
  imports: [
    CommonModule,
    GeneralModule,
    RouterModule
  ]
})
export class UnknownModule {
}
