import { Component, Input, OnInit } from '@angular/core';
import { MenuItem } from "primeng/api";

@Component({
  selector: 'app-event-types-tabmenu',
  templateUrl: './event-types-tabmenu.component.html'
})
export class EventTypesTabMenuComponent implements OnInit {
  public readonly SCROLLABLE_WIDTH = 150;

  @Input() private id: number;

  public items: MenuItem[] = [];

  ngOnInit(): void {
    this.items = [
      {label: 'eventTypes.search.title', routerLink: '/information/event-types/search'}
    ];

    if (!isNaN(this.id)) {
      this.items.push({label: 'eventType.title', routerLink: `/information/event-type/${this.id}`});
    }
  }
}
