import { Injectable } from '@angular/core';
import { Message } from './message.model';
import { Observable, Subject } from 'rxjs';

@Injectable()
export class MessageService {
  private subject = new Subject<Message>();

  private static getMessageText(response: Response): string {
    const error = response['error'];

    if (error) {
      const customMessage = error['customMessage'];

      if (customMessage) {
        return customMessage;
      }
    }

    return `Network Error: ${response.statusText} (${response.status})`;
  }

  reportMessage(parameter: Message | Response) {
    const msg = (parameter instanceof Message) ?
      parameter :
      new Message(MessageService.getMessageText(parameter), new Date(), true);

    this.subject.next(msg);
  }

  get messages(): Observable<Message> {
    return this.subject;
  }
}
