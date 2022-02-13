import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-companies-menubar',
  templateUrl: './companies-menubar.component.html'
})
export class CompaniesMenubarComponent implements OnInit {
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
