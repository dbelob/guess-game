import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { EventTypeStatistics } from '../models/event-type-statistics.model';
import { EventStatistics } from '../models/event-statistics.model';
import { MessageService } from '../../modules/message/message.service';

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {
  private baseUrl = 'api/statistics';

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  getEventTypeStatistics(conferences: boolean, meetups: boolean): Observable<EventTypeStatistics> {
    const params = new HttpParams()
      .set('conferences', conferences.toString())
      .set('meetups', meetups.toString());

    return this.http.get<EventTypeStatistics>(`${this.baseUrl}/event-types`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getEventStatistics(eventId: number): Observable<EventStatistics> {
    const params = new HttpParams();
    if (eventId) {
      params.set('eventId', eventId.toString());
    }

    return this.http.get<EventStatistics>(`${this.baseUrl}/events`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
