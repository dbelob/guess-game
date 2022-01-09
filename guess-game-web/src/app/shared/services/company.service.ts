import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Company } from '../models/company/company.model';
import { CompanyDetails } from '../models/company/company-details.model';
import { CompanySearchResult } from '../models/company/company-search-result.model';
import { SelectedEntities } from '../models/common/selected-entities.model';
import { MessageService } from '../../modules/message/message.service';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {
  private baseUrl = 'api/company';

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  getCompaniesByFirstLetters(firstLetters: string): Observable<Company[]> {
    const params = new HttpParams()
      .set('firstLetters', firstLetters);

    return this.http.get<Company[]>(`${this.baseUrl}/first-letters-companies`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getSelectedCompanies(selectedEntities: SelectedEntities): Observable<Company[]> {
    return this.http.post<Company[]>(`${this.baseUrl}/selected-companies`, selectedEntities)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
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

  getCompanies(name: string): Observable<CompanySearchResult[]> {
    let params = new HttpParams();
    if (name) {
      params = params.set('name', name.toString());
    }

    return this.http.get<CompanySearchResult[]>(`${this.baseUrl}/companies`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getCompany(id: number): Observable<CompanyDetails> {
    return this.http.get<CompanyDetails>(`${this.baseUrl}/company/${id}`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
