import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-information-switcher',
  templateUrl: './information-switcher.component.html'
})
export class InformationSwitcherComponent implements OnInit {
  @Input() private type: string;

  constructor() {
  }

  ngOnInit(): void {
  }

  isSpeakers(): boolean {
    return ('speakers' === this.type);
  }

  isStatistics(): boolean {
    return ('statistics' === this.type);
  }
}
