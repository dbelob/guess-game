import { Component } from '@angular/core';
import { MenuItem } from "primeng/api";

@Component({
    selector: 'app-events-tabmenu',
    templateUrl: './events-tabmenu.component.html'
})
export class EventsTabMenuComponent {
    public items: MenuItem[] = [];
    public activeItem: MenuItem;

    constructor() {
        this.items = [
            {label: 'events.search.title', routerLink: '/information/events/search'},
            {label: 'event.title'}
        ];

        this.activeItem = this.items[1];
    }
}
