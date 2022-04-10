import { Component, Input, OnInit } from '@angular/core';
import { MenuItem } from "primeng/api";

@Component({
    selector: 'app-speakers-tabmenu',
    templateUrl: './speakers-tabmenu.component.html'
})
export class SpeakersTabMenuComponent implements OnInit {
    @Input() private activeItemIndex: number;

    public items: MenuItem[] = [];
    public activeItem: MenuItem;

    ngOnInit(): void {
        this.items = [
            {label: 'speakers.list.title', routerLink: '/information/speakers/list'},
            {label: 'speakers.search.title', routerLink: '/information/speakers/search'}
        ];

        if (this.activeItemIndex === this.items.length) {
            this.items.push({label: 'speaker.title'});
        }

        if (this.activeItemIndex < this.items.length) {
            this.activeItem = this.items[this.activeItemIndex];
        }
    }
}
