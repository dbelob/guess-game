import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Language } from '../../shared/models/language.model';
import { LocaleService } from '../../shared/services/locale.service';

@Component({
  selector: 'app-language-switcher',
  templateUrl: './language-switcher.component.html'
})
export class LanguageSwitcherComponent implements OnInit {
  @Output() reload: EventEmitter<any> = new EventEmitter();

  public selectedLanguage: Language;
  public language = Language;

  constructor(private localeService: LocaleService) {
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
          this.reload.emit();
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
