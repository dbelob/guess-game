import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-events-menubar',
  templateUrl: './events-menubar.component.html'
})
export class EventsMenubarComponent implements OnInit {
  @Input() private type: string;

  constructor() {
  }

  ngOnInit(): void {
  }

  isSearch(): boolean {
    return ('search' === this.type);
  }

  isEvent(): boolean {
    return ('event' === this.type);
  }
}
