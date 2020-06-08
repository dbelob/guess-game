import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { Language } from '../../shared/models/language.model';
import { LocaleService } from '../../shared/services/locale.service';

@Component({
  selector: 'app-language-switcher',
  templateUrl: './language-switcher.component.html'
})
export class LanguageSwitcherComponent implements OnInit {
  public selectedLanguage: Language;
  public language = Language;
  @Output() onReload: EventEmitter<any> = new EventEmitter();

  constructor(private localeService: LocaleService, private router: Router) {
  }

  ngOnInit(): void {
    this.localeService.getLanguage()
      .subscribe(data => {
        this.selectedLanguage = data;
      });
  }

  onLanguageChange(language: Language) {
    this.localeService.setLanguage(language)
      .subscribe(data => {
          this.onReload.emit();
        }
      );
  }

  isEnChecked(): boolean {
    return this.selectedLanguage === Language.English;
  }

  isRuChecked(): boolean {
    return this.selectedLanguage === Language.Russian;
  }
}
