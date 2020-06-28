import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-event-types-switcher',
  templateUrl: './event-types-switcher.component.html'
})
export class EventTypesSwitcherComponent implements OnInit {
  @Input() private type: string;

  constructor() {
  }

  ngOnInit(): void {
  }

  isSearch(): boolean {
    return ('search' === this.type);
  }

  isEventType(): boolean {
    return ('eventType' === this.type);
  }
}
