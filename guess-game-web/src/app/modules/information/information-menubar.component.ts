import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-information-menubar',
  templateUrl: './information-menubar.component.html'
})
export class InformationMenubarComponent implements OnInit {
  @Input() private type: string;

  constructor() {
  }

  ngOnInit(): void {
  }

  isEventTypes(): boolean {
    return ('eventTypes' === this.type);
  }

  isEvents(): boolean {
    return ('events' === this.type);
  }

  isTalks(): boolean {
    return ('talks' === this.type);
  }

  isSpeakers(): boolean {
    return ('speakers' === this.type);
  }

  isCompanies(): boolean {
    return ('companies' === this.type);
  }

  isStatistics(): boolean {
    return ('statistics' === this.type);
  }
}
