import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-companies-switcher',
  templateUrl: './companies-switcher.component.html'
})
export class CompaniesSwitcherComponent implements OnInit {
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

  isCompany(): boolean {
    return ('company' === this.type);
  }
}
