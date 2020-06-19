import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-talks-switcher',
  templateUrl: './talks-switcher.component.html'
})
export class TalksSwitcherComponent implements OnInit {
  @Input() private type: string;

  constructor() {
  }

  ngOnInit(): void {
  }

  isSearch(): boolean {
    return ('search' === this.type);
  }

  isTalk(): boolean {
    return ('talk' === this.type);
  }
}
