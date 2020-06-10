import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { EventTypeStatistics } from '../models/event-type-statistics.model';
import { EventStatistics } from '../models/event-statistics.model';
import { MessageService } from '../../modules/message/message.service';
import { EventType } from "../models/event-type.model";

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

    return this.http.get<EventTypeStatistics>(`${this.baseUrl}/event-type-statistics`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getEventStatistics(eventTypeId: number): Observable<EventStatistics> {
    const params = new HttpParams();
    if (eventTypeId) {
      params.set('eventTypeId', eventTypeId.toString());
    }

    return this.http.get<EventStatistics>(`${this.baseUrl}/event-statistics`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getConferences(): Observable<EventType[]> {
    return this.http.get<EventType[]>(`${this.baseUrl}/conferences`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
