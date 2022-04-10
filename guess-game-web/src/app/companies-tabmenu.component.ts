import { Component, Input, OnInit } from '@angular/core';
import { MenuItem } from "primeng/api";

@Component({
    selector: 'app-companies-tabmenu',
    templateUrl: './companies-tabmenu.component.html'
})
export class CompaniesTabMenuComponent implements OnInit {
    @Input() private activeItemIndex: number;

    public items: MenuItem[] = [];
    public activeItem: MenuItem;

    ngOnInit(): void {
        this.items = [
            {label: 'companies.list.title', routerLink: '/information/companies/list'},
            {label: 'companies.search.title', routerLink: '/information/companies/search'}
        ];

        if (this.activeItemIndex === this.items.length) {
            this.items.push({label: 'company.title'});
        }

        if (this.activeItemIndex < this.items.length) {
            this.activeItem = this.items[this.activeItemIndex];
        }
    }
}
