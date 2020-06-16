import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Speaker } from '../models/speaker.model';
import { MessageService } from '../../modules/message/message.service';

@Injectable({
  providedIn: 'root'
})
export class SpeakerService {
  private baseUrl = 'api/speaker';

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  getSpeakersByFirstLetter(firstLetter: string): Observable<Speaker[]> {
    const params = new HttpParams()
      .set('firstLetter', firstLetter.toString());

    return this.http.get<Speaker[]>(`${this.baseUrl}/first-letter-speakers`, {params: params})
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
