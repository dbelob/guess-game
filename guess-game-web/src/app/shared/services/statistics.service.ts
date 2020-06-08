import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { EventTypeMetrics } from '../models/event-type-metrics.model';
import { MessageService } from '../../modules/message/message.service';

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {
  private baseUrl = 'api/statistics';

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  getEventTypeMetrics(conferences: boolean, meetups: boolean): Observable<EventTypeMetrics[]> {
    const params = new HttpParams()
      .set('conferences', conferences.toString())
      .set('meetups', meetups.toString());

    return this.http.get<EventTypeMetrics[]>(`${this.baseUrl}/event-types`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
