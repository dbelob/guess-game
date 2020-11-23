import { EventType } from './event-type.model';
import { Event } from '../event/event.model';

export class EventTypeDetails {
  constructor(
    public eventType?: EventType,
    public events?: Event[]
  ) {
  }
}
