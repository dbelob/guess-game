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
import { MeasureType } from '../models/statistics/olap/measure-type.model';
import { MessageService } from '../../modules/message/message.service';
import { OlapStatistics } from "../models/statistics/olap/olap-statistics.model";
import { OlapParameters } from "../models/statistics/olap/olap.parameters.model";
import { OlapEventTypeParameters } from "../models/statistics/olap/olap-event-type-parameters.model";
import { OlapEntityStatistics } from "../models/statistics/olap/olap-entity-statistics.model";
import { OlapEventTypeMetrics } from "../models/statistics/olap/olap-event-type-metrics.model";
import { OlapSpeakerMetrics } from '../models/statistics/olap/olap-speaker-metrics.model';
import { OlapSpeakerParameters } from '../models/statistics/olap/olap-speaker-parameters.model';
import { OlapCityMetrics } from '../models/statistics/olap/olap-city-metrics.model';
import { OlapCityParameters } from '../models/statistics/olap/olap-city-parameters.model';

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

  getEventStatistics(organizer: Organizer, eventType: EventType): Observable<EventStatistics> {
    let params = new HttpParams();
    if (organizer) {
      params = params.set('organizerId', organizer.id.toString());
    }
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

  getCubeTypes(): Observable<CubeType[]> {
    return this.http.get<CubeType[]>(`${this.baseUrl}/cube-types`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getMeasureTypes(cubeType: CubeType): Observable<MeasureType[]> {
    const params = new HttpParams()
      .set('cubeType', (cubeType) ? cubeType.toString() : null);

    return this.http.get<MeasureType[]>(`${this.baseUrl}/measure-types`, {params: params})
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

  getOlapEventTypeStatistics(olapParameters: OlapEventTypeParameters): Observable<OlapEntityStatistics<number, OlapEventTypeMetrics>> {
    return this.http.post<OlapEntityStatistics<number, OlapEventTypeMetrics>>(`${this.baseUrl}/olap-event-type-statistics`, olapParameters)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getOlapSpeakerStatistics(olapParameters: OlapSpeakerParameters): Observable<OlapEntityStatistics<number, OlapSpeakerMetrics>> {
    return this.http.post<OlapEntityStatistics<number, OlapSpeakerMetrics>>(`${this.baseUrl}/olap-speaker-statistics`, olapParameters)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getOlapCityStatistics(olapParameters: OlapCityParameters): Observable<OlapEntityStatistics<number, OlapCityMetrics>> {
    return this.http.post<OlapEntityStatistics<number, OlapCityMetrics>>(`${this.baseUrl}/olap-city-statistics`, olapParameters)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
