import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-talks-menubar',
  templateUrl: './talks-menubar.component.html'
})
export class TalksMenubarComponent implements OnInit {
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
