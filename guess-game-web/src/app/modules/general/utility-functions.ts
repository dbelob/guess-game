import { Event } from '../../shared/models/event.model';
import { EventType } from '../../shared/models/event-type.model';

export function findEventTypeByDefaultEvent(defaultEvent: Event, eventTypes: EventType[]): EventType {
  if (defaultEvent) {
    for (let i = 0; i < eventTypes.length; i++) {
      const eventType: EventType = eventTypes[i];

      if (defaultEvent.eventTypeId === eventType.id) {
        return eventType;
      }
    }
  }

  return null;
}

export function findEventByDefaultEvent(defaultEvent: Event, events: Event[]): Event {
  if (defaultEvent) {
    for (let i = 0; i < events.length; i++) {
      const event: Event = events[i];

      if (defaultEvent.id === event.id) {
        return event;
      }
    }
  }

  return null;
}
