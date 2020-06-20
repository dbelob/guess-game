import { formatDate } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { Event } from '../../shared/models/event.model';
import { EventType } from '../../shared/models/event-type.model';

export function isStringEmpty(value: string): boolean {
  return (!value || (value.trim().length <= 0));
}

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

export function isEventStartDateVisible(event: Event): boolean {
  return !!event.startDate;
}

export function isEventEndDateVisible(event: Event): boolean {
  return (event.startDate && event.endDate && (event.startDate !== event.endDate));
}

export function isEventDateParenthesesVisible(event: Event): boolean {
  return (isEventStartDateVisible(event) || isEventEndDateVisible(event));
}

export function isEventHyphenVisible(event: Event): boolean {
  return (isEventStartDateVisible(event) && isEventEndDateVisible(event));
}

export function getEventsWithDisplayName(events: Event[], translateService: TranslateService): Event[] {
  if (events) {
    for (let i = 0; i < events.length; i++) {
      const event: Event = events[i];
      const isEventDateParenthesesVisibleFlag = isEventDateParenthesesVisible(event);
      const isEventStartDateVisibleFlag = isEventStartDateVisible(event);
      const isEventHyphenVisibleFlag = isEventHyphenVisible(event);
      const isEventEndDateVisibleFlag = isEventEndDateVisible(event);

      let displayName = event.name;

      if (isEventDateParenthesesVisibleFlag) {
        displayName += ' (';
      }

      if (isEventStartDateVisibleFlag) {
        displayName += formatDate(event.startDate, 'shortDate', translateService.currentLang, undefined);
      }

      if (isEventHyphenVisibleFlag) {
        displayName += ' – ';
      }

      if (isEventEndDateVisibleFlag) {
        displayName += formatDate(event.endDate, 'shortDate', translateService.currentLang, undefined);
      }

      if (isEventDateParenthesesVisibleFlag) {
        displayName += ')';
      }

      event.displayName = displayName;
    }
  }

  return events;
}
