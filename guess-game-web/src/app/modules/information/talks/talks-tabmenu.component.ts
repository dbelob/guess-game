import { Component } from '@angular/core';
import { MenuItem } from "primeng/api";

@Component({
    selector: 'app-talks-tabmenu',
    templateUrl: './talks-tabmenu.component.html'
})
export class TalksTabMenuComponent {
    public items: MenuItem[] = [];
    public activeItem: MenuItem;

    constructor() {
        this.items = [
            {label: 'talks.search.title', routerLink: '/information/talks/search'},
            {label: 'talk.title'}
        ];

        this.activeItem = this.items[1];
    }
}
