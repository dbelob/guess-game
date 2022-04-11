import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { MenubarModule } from 'primeng/menubar';
import { TabViewModule } from 'primeng/tabview';
import { InformationMenubarComponent } from './information-menubar.component';

@NgModule({
  declarations: [
    InformationMenubarComponent
  ],
  imports: [
    CommonModule,
    MenubarModule,
    RouterModule,
    TabViewModule,
    TranslateModule
  ],
  exports: [
    InformationMenubarComponent
  ]
})
export class InformationModule {
}
