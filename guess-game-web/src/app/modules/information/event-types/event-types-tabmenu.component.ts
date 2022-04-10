import { Component } from '@angular/core';
import { MenuItem } from "primeng/api";

@Component({
    selector: 'app-event-types-tabmenu',
    templateUrl: './event-types-tabmenu.component.html'
})
export class EventTypesTabMenuComponent {
    public items: MenuItem[] = [];
    public activeItem: MenuItem;

    constructor() {
        this.items = [
            {label: 'eventTypes.search.title', routerLink: '/information/event-types/search'},
            {label: 'eventType.title'}
        ];

        this.activeItem = this.items[1];
    }
}
