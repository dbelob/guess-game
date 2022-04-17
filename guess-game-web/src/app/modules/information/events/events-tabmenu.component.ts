import { Component, Input, OnInit } from '@angular/core';
import { MenuItem } from "primeng/api";

@Component({
  selector: 'app-events-tabmenu',
  templateUrl: './events-tabmenu.component.html'
})
export class EventsTabMenuComponent implements OnInit {
  public readonly SCROLLABLE_WIDTH = 150;

  @Input() private id: number;

  public items: MenuItem[] = [];

  ngOnInit(): void {
    this.items = [
      {label: 'events.search.title', routerLink: '/information/events/search'}
    ];

    if (!isNaN(this.id)) {
      this.items.push({label: 'event.title', routerLink: `/information/event/${this.id}`});
    }
  }
}
