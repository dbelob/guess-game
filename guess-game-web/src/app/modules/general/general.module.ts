import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LanguageSwitcherComponent } from './language-switcher.component';
import { FocusRemoverDirective } from './focus-remover.directive';

@NgModule({
  declarations: [
    FocusRemoverDirective,
    LanguageSwitcherComponent
  ],
  imports: [
    CommonModule,
    FormsModule
  ],
  exports: [
    FocusRemoverDirective,
    LanguageSwitcherComponent
  ]
})
export class GeneralModule {
}
