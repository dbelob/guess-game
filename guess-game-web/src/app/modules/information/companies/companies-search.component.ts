import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CompanySearchResult } from '../../../shared/models/company/company-search-result.model';
import { CompanyService } from '../../../shared/services/company.service';
import { isStringEmpty } from '../../general/utility-functions';

@Component({
  selector: 'app-companies-search',
  templateUrl: './companies-search.component.html'
})
export class CompaniesSearchComponent {
  public name: string;

  public companies: CompanySearchResult[] = [];

  private searched = false;
  public multiSortMeta: any[] = [];

  constructor(private companyService: CompanyService, public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'name', order: 1});
  }

  loadCompanies(name: string) {
    this.companyService.getCompanies(name)
      .subscribe(data => {
        this.companies = data;
        this.searched = true;
      });
  }

  onLanguageChange() {
    this.search();
  }

  onFilterChange(value: any) {
    this.searched = false;
  }

  search() {
    if (!this.isSearchDisabled()) {
      this.loadCompanies(this.name);
    }
  }

  clear() {
    this.name = undefined;
    this.companies = [];

    this.searched = false;
  }

  isSearchDisabled(): boolean {
    return isStringEmpty(this.name);
  }

  isNoCompaniesFoundVisible() {
    return (this.searched && (this.companies.length === 0));
  }

  isCompaniesListVisible() {
    return (this.searched && (this.companies.length > 0));
  }
}
