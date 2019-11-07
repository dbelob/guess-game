import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { MessageService } from "../../modules/message/message.service";
import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";
import { Language } from "../models/language.model";

@Injectable({
  providedIn: 'root'
})
export class LocaleService {
  private baseUrl = 'api/locale';

  constructor(private http: HttpClient, private messageService: MessageService) {
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
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
