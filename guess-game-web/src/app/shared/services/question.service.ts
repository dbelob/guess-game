import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import { catchError } from "rxjs/operators";
import { MessageService } from "../../modules/message/message.service";
import { QuestionSet } from "../models/question-set.model";
import { Observable } from "rxjs";
import { EventType } from "../models/event-type.model";

@Injectable({
  providedIn: 'root'
})
export class QuestionService {
  private baseUrl = 'api/question';

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  getQuestionSets(): Observable<QuestionSet[]> {
    return this.http.get<QuestionSet[]>(`${this.baseUrl}/sets`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getDefaultQuestionSetId(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/default-set-id`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getEventTypes(): Observable<EventType[]> {
    return this.http.get<EventType[]>(`${this.baseUrl}/event-types`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getQuantities(questionSetIds: number[], guessType: string): Observable<number[]> {
    let params = new HttpParams()
      .set('questionSetIds', questionSetIds.toString())
      .set('guessType', guessType);

    return this.http.get<number[]>(`${this.baseUrl}/quantities`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
