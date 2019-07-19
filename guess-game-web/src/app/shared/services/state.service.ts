import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { catchError } from "rxjs/operators";
import { MessageService } from "../../modules/message/message.service";
import { StartParameters } from "../models/start-parameters.model";

@Injectable({
  providedIn: 'root'
})
export class StateService {
  private baseUrl = 'api/state';

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  setStartParameters(startParameters: StartParameters) {
    return this.http.post<string>(`${this.baseUrl}/parameters`, startParameters)
      .pipe(
        catchError((response: Response) => {
          this.messageService.reportMessage(response);
          throw response;
        })
      );
  }
}
