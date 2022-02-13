import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { InformationMenubarComponent } from './information-menubar.component';

@NgModule({
  declarations: [
    InformationMenubarComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    TranslateModule
  ],
  exports: [
    InformationMenubarComponent
  ]
})
export class InformationModule {
}
