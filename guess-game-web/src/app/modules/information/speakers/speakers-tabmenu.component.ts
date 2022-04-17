import { Component, Input, OnInit } from '@angular/core';
import { MenuItem } from "primeng/api";

@Component({
  selector: 'app-speakers-tabmenu',
  templateUrl: './speakers-tabmenu.component.html'
})
export class SpeakersTabMenuComponent implements OnInit {
  @Input() private id: number;

  public items: MenuItem[] = [];

  ngOnInit(): void {
    this.items = [
      {label: 'speakers.list.title', routerLink: '/information/speakers/list'},
      {label: 'speakers.search.title', routerLink: '/information/speakers/search'}
    ];

    if (!isNaN(this.id)) {
      this.items.push({label: 'speaker.title', routerLink: `/information/speaker/${this.id}`});
    }
  }
}
