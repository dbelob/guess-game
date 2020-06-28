import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-speakers-switcher',
  templateUrl: './speakers-switcher.component.html'
})
export class SpeakersSwitcherComponent implements OnInit {
  @Input() private type: string;

  constructor() {
  }

  ngOnInit(): void {
  }

  isList(): boolean {
    return ('list' === this.type);
  }

  isSearch(): boolean {
    return ('search' === this.type);
  }

  isSpeaker(): boolean {
    return ('speaker' === this.type);
  }
}
