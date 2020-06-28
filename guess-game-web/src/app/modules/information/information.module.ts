import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { InformationSwitcherComponent } from './information-switcher.component';

@NgModule({
  declarations: [
    InformationSwitcherComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    TranslateModule
  ],
  exports: [
    InformationSwitcherComponent
  ]
})
export class InformationModule {
}
