import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-event-types-menubar',
  templateUrl: './event-types-menubar.component.html'
})
export class EventTypesMenubarComponent implements OnInit {
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
