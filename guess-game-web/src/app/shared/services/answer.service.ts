import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { MessageService } from '../../modules/message/message.service';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Result } from '../models/result/result.model';

@Injectable({
  providedIn: 'root'
})
export class AnswerService {
  private baseUrl = 'api/answer';

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  setAnswer(questionIndex: number, answerId: number): Observable<string> {
    const params = new HttpParams()
      .set('questionIndex', questionIndex.toString())
      .set('answerId', answerId.toString());

    return this.http.post<string>(`${this.baseUrl}/answers`, {}, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getResult(): Observable<Result> {
    return this.http.get<Result>(`${this.baseUrl}/result`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
