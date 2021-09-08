import { formatDate } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { Event } from '../../shared/models/event/event.model';
import { EventType } from '../../shared/models/event-type/event-type.model';
import { Talk } from '../../shared/models/talk/talk.model';
import { Speaker } from '../../shared/models/speaker/speaker.model';
import { EventTypeMetrics } from '../../shared/models/statistics/event-type-metrics.model';
import { EventTypeStatistics } from '../../shared/models/statistics/event-type-statistics.model';
import { Organizer } from '../../shared/models/organizer/organizer.model';
import { OlapEntityStatistics } from "../../shared/models/statistics/olap/olap-entity-statistics.model";
import { OlapEventTypeMetrics } from "../../shared/models/statistics/olap/olap-event-type-metrics.model";

export function isStringEmpty(value: string): boolean {
  return (!value || (value.trim().length <= 0));
}

export function findOrganizerById(id: number, organizers: Organizer[]): Organizer {
  for (let i = 0; i < organizers.length; i++) {
    const organizer: Organizer = organizers[i];

    if (id === organizer.id) {
      return organizer;
    }
  }

  return null;
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

export function findEventTypesByIds(ids: number[], eventTypes: EventType[]): EventType[] {
  const result: EventType[] = [];

  for (let id of ids) {
    const eventType = findEventTypeById(id, eventTypes);

    if (eventType) {
      result.push(eventType);
    }
  }

  return result;
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

export function getEventDates(event: Event, translateService: TranslateService): string {
  const isEventStartDateVisibleFlag = isEventStartDateVisible(event);
  const isEventHyphenVisibleFlag = isEventHyphenVisible(event);
  const isEventEndDateVisibleFlag = isEventEndDateVisible(event);

  let result = '';

  if (isEventStartDateVisibleFlag) {
    result += formatDate(event.startDate, 'shortDate', translateService.currentLang, undefined);
  }

  if (isEventHyphenVisibleFlag) {
    result += ' – ';
  }

  if (isEventEndDateVisibleFlag) {
    result += formatDate(event.endDate, 'shortDate', translateService.currentLang, undefined);
  }

  return result;
}

export function getEventDisplayName(event: Event, translateService: TranslateService): string {
  const isEventDateParenthesesVisibleFlag = isEventDateParenthesesVisible(event);

  let displayName = event.name;

  if (isEventDateParenthesesVisibleFlag) {
    displayName += ' (';
  }

  displayName += getEventDates(event, translateService);

  if (isEventDateParenthesesVisibleFlag) {
    displayName += ')';
  }

  return displayName;
}

export function getEventTypesWithSortName(eventTypes: EventType[]): EventType[] {
  if (eventTypes) {
    for (let i = 0; i < eventTypes.length; i++) {
      const eventType: EventType = eventTypes[i];

      eventType.sortName = (eventType.conference ? '0' : '1') + eventType.organizerName + eventType.name;
    }
  }

  return eventTypes;
}

export function getEventTypeStatisticsWithSortName(eventTypeStatistics: EventTypeStatistics): EventTypeStatistics {
  const eventTypeMetricsList: EventTypeMetrics[] = eventTypeStatistics.eventTypeMetricsList;

  if (eventTypeMetricsList) {
    for (let i = 0; i < eventTypeMetricsList.length; i++) {
      const eventTypeMetrics: EventTypeMetrics = eventTypeMetricsList[i];

      eventTypeMetrics.sortName = (eventTypeMetrics.conference ? '0' : '1') + eventTypeMetrics.organizerName + eventTypeMetrics.displayName;
    }
  }

  return eventTypeStatistics;
}

export function getOlapEventTypeStatisticsWithSortName(eventTypeStatistics: OlapEntityStatistics<number, OlapEventTypeMetrics>): OlapEntityStatistics<number, OlapEventTypeMetrics> {
  const eventTypeMetricsList: OlapEventTypeMetrics[] = eventTypeStatistics.metricsList;

  if (eventTypeMetricsList) {
    for (let i = 0; i < eventTypeMetricsList.length; i++) {
      const eventTypeMetrics: OlapEventTypeMetrics = eventTypeMetricsList[i];

      eventTypeMetrics.sortName = (eventTypeMetrics.conference ? '0' : '1') + eventTypeMetrics.organizerName + eventTypeMetrics.displayName;
    }
  }

  return eventTypeStatistics;
}

export function getEventsWithBriefDisplayName(events: Event[]): Event[] {
  if (events) {
    for (let i = 0; i < events.length; i++) {
      events[i].displayName = events[i].name;
    }
  }

  return events;
}

export function getEventsWithFullDisplayName(events: Event[], translateService: TranslateService): Event[] {
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

export function getSpeakersWithCompaniesString(speakers: Speaker[]): Speaker[] {
  if (speakers) {
    speakers.forEach(s => {
      s.companiesString = s.companies.map(c => c.name).join(', ');
    });
  }

  return speakers;
}
