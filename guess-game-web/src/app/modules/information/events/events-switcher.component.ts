import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-events-switcher',
  templateUrl: './events-switcher.component.html'
})
export class EventsSwitcherComponent implements OnInit {
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
