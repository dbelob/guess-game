import { Component } from '@angular/core';
import { MenuItem } from "primeng/api";

@Component({
  selector: 'app-statistics-tabmenu',
  templateUrl: './statistics-tabmenu.component.html'
})
export class StatisticsTabMenuComponent {
  public readonly SCROLLABLE_WIDTH = 360;

  public items: MenuItem[] = [
    {label: 'statistics.eventTypes.title', routerLink: '/information/statistics/event-types'},
    {label: 'statistics.events.title', routerLink: '/information/statistics/events'},
    {label: 'statistics.speakers.title', routerLink: '/information/statistics/speakers'},
    {label: 'statistics.companies.title', routerLink: '/information/statistics/companies'},
    {label: 'statistics.olap.title', routerLink: '/information/statistics/olap'}
  ];
}
