import { Component, Input, OnInit } from '@angular/core';
import { MenuItem } from "primeng/api";

@Component({
  selector: 'app-talks-tabmenu',
  templateUrl: './talks-tabmenu.component.html'
})
export class TalksTabMenuComponent implements OnInit {
  @Input() private id: number;

  public items: MenuItem[] = [];

  ngOnInit(): void {
    this.items = [
      {label: 'talks.search.title', routerLink: '/information/talks/search'}
    ];

    if (!isNaN(this.id)) {
      this.items.push({label: 'talk.title', routerLink: `/information/talk/${this.id}`});
    }
  }
}
