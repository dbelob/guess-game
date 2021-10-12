import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-chart-type-switcher',
  templateUrl: './chart-type-switcher.component.html'
})
export class ChartTypeSwitcherComponent implements OnInit {
  @Input() private type: string;
  @Output() detailsClick: EventEmitter<any> = new EventEmitter();
  @Output() totalClick: EventEmitter<any> = new EventEmitter();

  constructor() {
  }

  ngOnInit(): void {
  }

  isDetails(): boolean {
    return ('details' === this.type);
  }

  isTotal(): boolean {
    return ('total' === this.type);
  }
}
