import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CompanySearchResult } from '../../../shared/models/company/company-search-result.model';
import { CompanyService } from '../../../shared/services/company.service';

@Component({
  selector: 'app-companies-list',
  templateUrl: './companies-list.component.html'
})
export class CompaniesListComponent implements OnInit {
  private readonly DEFAULT_IS_DIGIT = false;
  private readonly DEFAULT_LETTER = 'A';
  private readonly RUSSIAN_LANG = 'ru';
  public readonly DIGIT_BUTTON_TEXT = '0-9';

  public enLetters: string[] = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
    'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
  public ruLetters: string[] = ['А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р',
    'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Э', 'Ю', 'Я'];

  public isDigit = this.DEFAULT_IS_DIGIT;
  public selectedLetter = this.DEFAULT_LETTER;

  public companies: CompanySearchResult[] = [];
  public paginatorFirst = 0;

  constructor(private companyService: CompanyService, public translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.loadCompanies(this.isDigit, this.selectedLetter);
  }

  loadCompanies(isDigit: boolean, letter: string) {
    // TODO: implement
    console.log('isDigit: ' + ', letter: ' + letter);
  }

  onLanguageChange() {
    this.changeLetter(this.DEFAULT_LETTER);
  }

  isCurrentLetter(letter: string) {
    return !this.isDigit && (this.selectedLetter === letter);
  }

  changeLetter(letter: string) {
    this.isDigit = false;
    this.selectedLetter = letter;

    this.paginatorFirst = 0;

    this.loadCompanies(this.isDigit, letter);
  }

  changeDigit() {
    this.isDigit = true;
    this.selectedLetter = null;
  }

  isRuLettersVisible(): boolean {
    return (this.translateService.currentLang === this.RUSSIAN_LANG);
  }

  isCompaniesListVisible() {
    return (this.companies.length > 0);
  }
}
