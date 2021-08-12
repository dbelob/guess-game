import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { EventType } from '../models/event-type/event-type.model';
import { EventTypeStatistics } from '../models/statistics/event-type-statistics.model';
import { EventStatistics } from '../models/statistics/event-statistics.model';
import { SpeakerStatistics } from '../models/statistics/speaker-statistics.model';
import { CompanyStatistics } from '../models/statistics/company-statistics.model';
import { Organizer } from '../models/organizer/organizer.model';
import { CubeType } from '../models/statistics/olap/cube-type.model';
import { Measure } from '../models/statistics/olap/measure.model';
import { MessageService } from '../../modules/message/message.service';
import { OlapStatistics } from "../models/statistics/olap/olap-statistics.model";
import { OlapParameters } from "../models/statistics/olap/olap.parameters.model";

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {
  private baseUrl = 'api/statistics';

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  getEventTypeStatistics(conferences: boolean, meetups: boolean, organizer: Organizer): Observable<EventTypeStatistics> {
    let params = new HttpParams()
      .set('conferences', conferences.toString())
      .set('meetups', meetups.toString());
    if (organizer) {
      params = params.set('organizerId', organizer.id.toString());
    }

    return this.http.get<EventTypeStatistics>(`${this.baseUrl}/event-type-statistics`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getEventStatistics(eventType: EventType): Observable<EventStatistics> {
    let params = new HttpParams();
    if (eventType) {
      params = params.set('eventTypeId', eventType.id.toString());
    }

    return this.http.get<EventStatistics>(`${this.baseUrl}/event-statistics`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getSpeakerStatistics(conferences: boolean, meetups: boolean, organizer: Organizer, eventType: EventType): Observable<SpeakerStatistics> {
    let params = new HttpParams()
      .set('conferences', conferences.toString())
      .set('meetups', meetups.toString());
    if (organizer) {
      params = params.set('organizerId', organizer.id.toString());
    }
    if (eventType) {
      params = params.set('eventTypeId', eventType.id.toString());
    }

    return this.http.get<SpeakerStatistics>(`${this.baseUrl}/speaker-statistics`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getCompanyStatistics(conferences: boolean, meetups: boolean, organizer: Organizer, eventType: EventType): Observable<CompanyStatistics> {
    let params = new HttpParams()
      .set('conferences', conferences.toString())
      .set('meetups', meetups.toString());
    if (organizer) {
      params = params.set('organizerId', organizer.id.toString());
    }
    if (eventType) {
      params = params.set('eventTypeId', eventType.id.toString());
    }

    return this.http.get<CompanyStatistics>(`${this.baseUrl}/company-statistics`, {params: params})
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

  getCubeTypes(): Observable<CubeType[]> {
    return this.http.get<CubeType[]>(`${this.baseUrl}/cube-types`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getMeasures(cube: CubeType): Observable<Measure[]> {
    const params = new HttpParams()
      .set('cube', (cube) ? cube.toString() : null);

    return this.http.get<Measure[]>(`${this.baseUrl}/measures`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getOlapStatistics(olapParameters: OlapParameters): Observable<OlapStatistics> {
    return this.http.post<OlapStatistics>(`${this.baseUrl}/olap-statistics`, olapParameters)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
