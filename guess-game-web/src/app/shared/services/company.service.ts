import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MessageService } from '../../modules/message/message.service';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {
  private baseUrl = 'api/company';

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  getCompanyNamesByFirstLetters(firstLetters: string): Observable<string[]> {
    const params = new HttpParams()
      .set('firstLetters', firstLetters);

    return this.http.get<string[]>(`${this.baseUrl}/first-letters-company-names`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
