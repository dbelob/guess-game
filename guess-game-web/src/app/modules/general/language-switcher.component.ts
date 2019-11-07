import { Component, EventEmitter, Output } from '@angular/core';
import { Router } from "@angular/router";
import { Language } from "../../shared/models/language.model";
import { LocaleService } from "../../shared/services/locale.service";

@Component({
  selector: 'app-language-switcher',
  templateUrl: './language-switcher.component.html'
})
export class LanguageSwitcherComponent {
  public selectedLanguage: Language;
  public language = Language;
  @Output() onReload: EventEmitter<any> = new EventEmitter();

  constructor(private localeService: LocaleService, private router: Router) {
    localeService.getLanguage()
      .subscribe(data => {
        this.selectedLanguage = data;
      });
  }

  onLanguageChange(language: Language) {
    this.localeService.setLanguage(language)
      .subscribe(date => {
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
