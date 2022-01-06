import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-companies-list',
  templateUrl: './companies-list.component.html'
})
export class CompaniesListComponent implements OnInit {
  constructor(public translateService: TranslateService) {
  }

  ngOnInit(): void {
  }

  onLanguageChange() {
    // TODO: implement
  }
}
