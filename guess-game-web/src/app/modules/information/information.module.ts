import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { MenubarModule } from 'primeng/menubar';
import { TabMenuModule } from 'primeng/tabmenu';
import { TabViewModule } from 'primeng/tabview';
import { GeneralModule } from '../general/general.module';
import { InformationMenubarComponent } from './information-menubar.component';
import { InformationTabMenuComponent } from './information-tabmenu.component';

@NgModule({
  declarations: [
    InformationMenubarComponent,
    InformationTabMenuComponent
  ],
  imports: [
    CommonModule,
    MenubarModule,
    RouterModule,
    TabMenuModule,
    TabViewModule,
    TranslateModule,
    GeneralModule
  ],
  exports: [
    InformationMenubarComponent,
    InformationTabMenuComponent
  ]
})
export class InformationModule {
}
