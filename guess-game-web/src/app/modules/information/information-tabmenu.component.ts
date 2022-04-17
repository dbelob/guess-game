import { AfterViewInit, Component, ElementRef, Input, OnDestroy, ViewChild } from '@angular/core';
import { MenuItem } from "primeng/api";

@Component({
  selector: 'app-information-tabmenu',
  templateUrl: './information-tabmenu.component.html'
})
export class InformationTabMenuComponent implements AfterViewInit, OnDestroy {
  @Input() public items: MenuItem[] = [];
  @Input() public scrollableWidth: number;

  public scrollable = false;

  @ViewChild('tabMenuDiv') tabMenuDiv: ElementRef<HTMLDivElement>;

  ngAfterViewInit(): void {
    this.onResize();
    window.addEventListener('resize', this.onResize);
  }

  ngOnDestroy(): void {
    window.removeEventListener('resize', this.onResize);
  }

  onResize = (): void => {
    if (!isNaN(this.scrollableWidth) && this.tabMenuDiv) {
      this.scrollable = (this.tabMenuDiv.nativeElement.clientWidth < this.scrollableWidth);
    }
  }
}
