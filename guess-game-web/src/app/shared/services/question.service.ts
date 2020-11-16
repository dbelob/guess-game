import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { EventType } from '../models/event-type/event-type.model';
import { Event } from '../models/event/event.model';
import { MessageService } from '../../modules/message/message.service';

@Injectable({
  providedIn: 'root'
})
export class QuestionService {
  private baseUrl = 'api/question';

  constructor(private http: HttpClient, private messageService: MessageService) {
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

  getEvents(eventTypeIds: number[]): Observable<Event[]> {
    const params = new HttpParams()
      .set('eventTypeIds', eventTypeIds.toString());

    return this.http.get<Event[]>(`${this.baseUrl}/events`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getQuantities(eventTypeIds: number[], eventIds: number[], guessMode: string): Observable<number[]> {
    const params = new HttpParams()
      .set('eventTypeIds', eventTypeIds.toString())
      .set('eventIds', eventIds.toString())
      .set('guessMode', guessMode);

    return this.http.get<number[]>(`${this.baseUrl}/quantities`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
