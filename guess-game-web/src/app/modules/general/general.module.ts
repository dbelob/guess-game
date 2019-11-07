import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LanguageSwitcherComponent } from "./language-switcher.component";
import { FormsModule } from "@angular/forms";

@NgModule({
  declarations: [
    LanguageSwitcherComponent
  ],
  imports: [
    CommonModule,
    FormsModule
  ],
  exports: [
    LanguageSwitcherComponent
  ]
})
export class GeneralModule {
}
