import { formatDate } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { Event } from '../../shared/models/event.model';
import { EventType } from '../../shared/models/event-type.model';
import { Talk } from '../../shared/models/talk.model';

export function isStringEmpty(value: string): boolean {
  return (!value || (value.trim().length <= 0));
}

export function findEventTypeById(id: number, eventTypes: EventType[]): EventType {
  for (let i = 0; i < eventTypes.length; i++) {
    const eventType: EventType = eventTypes[i];

    if (id === eventType.id) {
      return eventType;
    }
  }

  return null;
}

export function findEventById(id: number, events: Event[]): Event {
  for (let i = 0; i < events.length; i++) {
    const event: Event = events[i];

    if (id === event.id) {
      return event;
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

export function getEventDisplayName(event: Event, translateService: TranslateService): string {
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

  return displayName;
}

export function getEventsWithDisplayName(events: Event[], translateService: TranslateService): Event[] {
  if (events) {
    for (let i = 0; i < events.length; i++) {
      events[i].displayName = getEventDisplayName(events[i], translateService);
    }
  }

  return events;
}

export function getTalksWithSpeakersString(talks: Talk[]): Talk[] {
  if (talks) {
    talks.forEach(t => {
      t.speakersString = t.speakers.map(s => s.displayName).join(', ');
    });
  }

  return talks;
}
