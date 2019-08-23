import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { CommonInfoComponent } from './common-info.component';

@NgModule({
  declarations: [CommonInfoComponent],
  imports: [CommonModule],
  exports: [CommonInfoComponent]
})
export class CommonInfoModule {
}
