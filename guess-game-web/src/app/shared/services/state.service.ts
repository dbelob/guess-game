import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { MessageService } from '../../modules/message/message.service';
import { StartParameters } from '../models/start-parameters.model';
import { State } from '../models/state.model';
import { PhotoNames } from '../models/photo-names.model';
import { NamePhotos } from '../models/name-photos.model';
import { SpeakersTalks } from '../models/speakers-talks.model';
import { TalkSpeakers } from '../models/talk-speakers.model';
import { SpeakerCompanies } from '../models/speaker-companies.model';
import { CompanySpeakers } from '../models/company-speakers.model';
import { SpeakerAccounts } from '../models/speaker-accounts.model';
import { AccountSpeakers } from '../models/account-speakers.model';

@Injectable({
  providedIn: 'root'
})
export class StateService {
  private baseUrl = 'api/state';

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  setStartParameters(startParameters: StartParameters): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}/parameters`, startParameters)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getState(): Observable<State> {
    return this.http.get<State>(`${this.baseUrl}/state`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  setState(state: State): Observable<string> {
    return this.http.put<string>(`${this.baseUrl}/state`, state)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getPhotoNames(): Observable<PhotoNames> {
    return this.http.get<PhotoNames>(`${this.baseUrl}/photo-names`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getNamePhotos(): Observable<NamePhotos> {
    return this.http.get<NamePhotos>(`${this.baseUrl}/name-photos`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getSpeakerTalks(): Observable<SpeakersTalks> {
    return this.http.get<SpeakersTalks>(`${this.baseUrl}/speaker-talks`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getTalkSpeakers(): Observable<TalkSpeakers> {
    return this.http.get<TalkSpeakers>(`${this.baseUrl}/talk-speakers`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getSpeakerCompanies(): Observable<SpeakerCompanies> {
    return this.http.get<SpeakerCompanies>(`${this.baseUrl}/speaker-companies`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getCompanySpeakers(): Observable<CompanySpeakers> {
    return this.http.get<CompanySpeakers>(`${this.baseUrl}/company-speakers`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getSpeakerAccounts(): Observable<SpeakerAccounts> {
    return this.http.get<SpeakerAccounts>(`${this.baseUrl}/speaker-accounts`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }

  getAccountSpeakers(): Observable<AccountSpeakers> {
    return this.http.get<AccountSpeakers>(`${this.baseUrl}/account-speakers`)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
