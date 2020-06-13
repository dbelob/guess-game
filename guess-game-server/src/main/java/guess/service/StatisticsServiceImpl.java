package guess.service;

import guess.dao.EventDao;
import guess.dao.EventTypeDao;
import guess.domain.source.Event;
import guess.domain.source.EventType;
import guess.domain.source.Speaker;
import guess.domain.source.Talk;
import guess.domain.statistics.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Statistics service implementation.
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final EventTypeDao eventTypeDao;
    private final EventDao eventDao;

    static class SpeakerMetricsInternal {
        private final Set<Talk> talks = new HashSet<>();
        private final Set<Event> events = new HashSet<>();
        private final Set<EventType> eventTypes = new HashSet<>();

        public Set<Talk> getTalks() {
            return talks;
        }

        public Set<Event> getEvents() {
            return events;
        }

        public Set<EventType> getEventTypes() {
            return eventTypes;
        }
    }

    @Autowired
    public StatisticsServiceImpl(EventTypeDao eventTypeDao, EventDao eventDao) {
        this.eventTypeDao = eventTypeDao;
        this.eventDao = eventDao;
    }

    @Override
    public EventTypeStatistics getEventTypeStatistics(boolean isConferences, boolean isMeetups) {
        List<EventType> eventTypes = eventTypeDao.getEventTypes().stream()
                .filter(et -> ((isConferences && et.isEventTypeConference()) || (isMeetups && !et.isEventTypeConference())))
                .collect(Collectors.toList());
        List<EventTypeMetrics> eventTypeMetricsList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate totalsStartDate = currentDate;
        long totalsDuration = 0;
        long totalsEventsQuantity = 0;
        long totalsTalksQuantity = 0;
        Set<Speaker> totalsSpeakers = new HashSet<>();

        for (EventType eventType : eventTypes) {
            // Event type metrics
            LocalDate eventTypeStartDate = currentDate;
            long eventTypeDuration = 0;
            long eventTypeTalksQuantity = 0;
            Set<Speaker> eventTypeSpeakers = new HashSet<>();

            for (Event event : eventType.getEvents()) {
                if (event.getStartDate().isBefore(eventTypeStartDate)) {
                    eventTypeStartDate = event.getStartDate();
                }

                eventTypeDuration += (ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1);
                eventTypeTalksQuantity += event.getTalks().size();
                event.getTalks().forEach(t -> eventTypeSpeakers.addAll(t.getSpeakers()));
            }

            eventTypeMetricsList.add(new EventTypeMetrics(
                    eventType,
                    eventTypeStartDate,
                    ChronoUnit.YEARS.between(eventTypeStartDate, currentDate),
                    eventTypeDuration,
                    eventType.getEvents().size(),
                    eventTypeTalksQuantity,
                    eventTypeSpeakers.size()));

            // Totals metrics
            if (eventTypeStartDate.isBefore(totalsStartDate)) {
                totalsStartDate = eventTypeStartDate;
            }

            totalsDuration += eventTypeDuration;
            totalsEventsQuantity += eventType.getEvents().size();
            totalsTalksQuantity += eventTypeTalksQuantity;
            totalsSpeakers.addAll(eventTypeSpeakers);
        }

        return new EventTypeStatistics(
                eventTypeMetricsList,
                new EventTypeMetrics(
                        new EventType(),
                        totalsStartDate,
                        ChronoUnit.YEARS.between(totalsStartDate, currentDate),
                        totalsDuration,
                        totalsEventsQuantity,
                        totalsTalksQuantity,
                        totalsSpeakers.size()));
    }

    @Override
    public EventStatistics getEventStatistics(Long eventTypeId) {
        List<Event> events = eventDao.getEvents().stream()
                .filter(e ->
                        (e.getEventType().isEventTypeConference() &&
                                ((eventTypeId == null) || (e.getEventType().getId() == eventTypeId))))
                .collect(Collectors.toList());
        List<EventMetrics> eventMetricsList = new ArrayList<>();
        LocalDate totalsStartDate = LocalDate.now();
        long totalsDuration = 0;
        long totalsTalksQuantity = 0;
        Set<Speaker> totalsSpeakers = new HashSet<>();

        for (Event event : events) {
            // Event metrics
            long eventDuration = (ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1);
            long eventTalksQuantity = event.getTalks().size();
            Set<Speaker> eventSpeakers = new HashSet<>();

            event.getTalks().forEach(t -> eventSpeakers.addAll(t.getSpeakers()));

            long eventJavaChampionsQuantity = eventSpeakers.stream()
                    .filter(Speaker::isJavaChampion)
                    .count();
            long eventMvpsQuantity = eventSpeakers.stream()
                    .filter(Speaker::isAnyMvp)
                    .count();

            eventMetricsList.add(new EventMetrics(
                    event,
                    event.getStartDate(),
                    eventDuration,
                    eventTalksQuantity,
                    eventSpeakers.size(),
                    eventJavaChampionsQuantity,
                    eventMvpsQuantity));

            // Totals metrics
            if (event.getStartDate().isBefore(totalsStartDate)) {
                totalsStartDate = event.getStartDate();
            }

            totalsDuration += eventDuration;
            totalsTalksQuantity += eventTalksQuantity;
            totalsSpeakers.addAll(eventSpeakers);
        }

        long totalsJavaChampionsQuantity = totalsSpeakers.stream()
                .filter(Speaker::isJavaChampion)
                .count();
        long totalsMvpsQuantity = totalsSpeakers.stream()
                .filter(Speaker::isAnyMvp)
                .count();

        return new EventStatistics(
                eventMetricsList,
                new EventMetrics(
                        new Event(),
                        totalsStartDate,
                        totalsDuration,
                        totalsTalksQuantity,
                        totalsSpeakers.size(),
                        totalsJavaChampionsQuantity,
                        totalsMvpsQuantity));
    }

    @Override
    public SpeakerStatistics getSpeakerStatistics(boolean isConferences, boolean isMeetups, Long eventTypeId) {
        List<EventType> eventTypes = eventTypeDao.getEventTypes().stream()
                .filter(et -> ((isConferences && et.isEventTypeConference()) || (isMeetups && !et.isEventTypeConference())) &&
                        ((eventTypeId == null) || (et.getId() == eventTypeId)))
                .collect(Collectors.toList());
        Map<Speaker, SpeakerMetricsInternal> speakerSpeakerMetricsMap = new HashMap<>();
        long totalsTalksQuantity = 0;
        long totalsEventsQuantity = 0;

        for (EventType eventType : eventTypes) {
            for (Event event : eventType.getEvents()) {
                for (Talk talk : event.getTalks()) {
                    for (Speaker speaker : talk.getSpeakers()) {
                        SpeakerMetricsInternal speakerMetricsInternal = speakerSpeakerMetricsMap.get(speaker);

                        // Speaker metrics
                        if (speakerMetricsInternal == null) {
                            speakerMetricsInternal = new SpeakerMetricsInternal();
                            speakerSpeakerMetricsMap.put(speaker, speakerMetricsInternal);
                        }

                        speakerMetricsInternal.getTalks().add(talk);
                        speakerMetricsInternal.getEvents().add(event);
                        speakerMetricsInternal.getEventTypes().add(eventType);
                    }
                }

                totalsTalksQuantity += event.getTalks().size();
            }

            totalsEventsQuantity += eventType.getEvents().size();
        }

        List<SpeakerMetrics> speakerMetricsList = new ArrayList<>();

        for (Speaker speaker : speakerSpeakerMetricsMap.keySet()) {
            SpeakerMetricsInternal speakerMetricsInternal = speakerSpeakerMetricsMap.get(speaker);

            speakerMetricsList.add(new SpeakerMetrics(
                    speaker,
                    speakerMetricsInternal.getTalks().size(),
                    speakerMetricsInternal.getEvents().size(),
                    speakerMetricsInternal.getEventTypes().size(),
                    speaker.isJavaChampion() ? 1 : 0,
                    speaker.isAnyMvp() ? 1 : 0));
        }

        // Totals metrics
        long totalsJavaChampionsQuantity = speakerSpeakerMetricsMap.keySet().stream()
                .filter(Speaker::isJavaChampion)
                .count();
        long totalsMvpsQuantity = speakerSpeakerMetricsMap.keySet().stream()
                .filter(Speaker::isAnyMvp)
                .count();

        return new SpeakerStatistics(
                speakerMetricsList,
                new SpeakerMetrics(
                        new Speaker(),
                        totalsTalksQuantity,
                        totalsEventsQuantity,
                        eventTypes.size(),
                        totalsJavaChampionsQuantity,
                        totalsMvpsQuantity)
        );
    }

    @Override
    public List<EventType> getConferences() {
        return eventTypeDao.getEventTypes().stream()
                .filter(EventType::isEventTypeConference)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventType> getEventTypes(boolean isConferences, boolean isMeetups) {
        return eventTypeDao.getEventTypes().stream()
                .filter(et -> ((isConferences && et.isEventTypeConference()) || (isMeetups && !et.isEventTypeConference())))
                .collect(Collectors.toList());
    }
}
