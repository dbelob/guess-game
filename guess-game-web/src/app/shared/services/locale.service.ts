import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { MessageService } from "../../modules/message/message.service";
import { Observable } from "rxjs";
import { catchError, map } from "rxjs/operators";
import { Language } from "../models/language.model";
import { TranslateService } from "@ngx-translate/core";

@Injectable({
  providedIn: 'root'
})
export class LocaleService {
  private baseUrl = 'api/locale';

  constructor(private http: HttpClient, private messageService: MessageService,
              public translateService: TranslateService) {
    this.translateService.addLangs(['en', 'ru']);
    this.translateService.setDefaultLang('en');

    this.getLanguageAndChangeInterfaceLanguage();
  }

  getLanguage(): Observable<Language> {
    return this.http.get<Language>(`${this.baseUrl}/language`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  setLanguage(language: Language): Observable<string> {
    return this.http.put<string>(`${this.baseUrl}/language`, language)
      .pipe(
        map(data => {
            this.changeInterfaceLanguage(language);
            return data;
          }
        ),
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  async getLanguageAndChangeInterfaceLanguage() {
    let language = await this.getLanguage().toPromise();

    this.changeInterfaceLanguage(language);
  }

  async changeInterfaceLanguage(language: Language) {
    await this.translateService.use(language === Language.Russian ? 'ru' : 'en').toPromise();
  }
}
