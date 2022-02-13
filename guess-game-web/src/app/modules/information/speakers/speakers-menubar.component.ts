import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-speakers-menubar',
  templateUrl: './speakers-menubar.component.html'
})
export class SpeakersMenubarComponent implements OnInit {
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
