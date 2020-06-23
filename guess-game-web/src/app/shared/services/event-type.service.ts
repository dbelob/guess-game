import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { EventType } from '../models/event-type.model';
import { MessageService } from '../../modules/message/message.service';

@Injectable({
  providedIn: 'root'
})
export class EventTypeService {
  private baseUrl = 'api/event-type';

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

  getFilterEventTypes(isConferences: boolean, isMeetups: boolean): Observable<EventType[]> {
    const params = new HttpParams()
      .set('conferences', isConferences.toString())
      .set('meetups', isMeetups.toString());

    return this.http.get<EventType[]>(`${this.baseUrl}/filter-event-types`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
