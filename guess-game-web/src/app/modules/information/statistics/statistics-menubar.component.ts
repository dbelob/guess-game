import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-statistics-menubar',
  templateUrl: './statistics-menubar.component.html'
})
export class StatisticsMenubarComponent implements OnInit {
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

  isSpeakers(): boolean {
    return ('speakers' === this.type);
  }

  isCompanies(): boolean {
    return ('companies' === this.type);
  }

  isOlap(): boolean {
    return ('olap' === this.type);
  }
}
