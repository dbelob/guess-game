package guess.dao;

import guess.domain.source.*;
import guess.domain.statistics.olap.Cube;
import guess.domain.statistics.olap.CubeType;
import guess.domain.statistics.olap.DimensionType;
import guess.domain.statistics.olap.MeasureType;
import guess.domain.statistics.olap.dimension.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * OLAP DAO implementation.
 */
@Repository
public class OlapDaoImpl implements OlapDao {
    private final Map<CubeType, Cube> cubes = new EnumMap<>(CubeType.class);

    private final EventTypeDao eventTypeDao;

    @Autowired
    public OlapDaoImpl(EventTypeDao eventTypeDao) {
        this.eventTypeDao = eventTypeDao;

        Cube eventTypesCube = new Cube(
                new LinkedHashSet<>(Arrays.asList(DimensionType.EVENT_TYPE, DimensionType.YEAR)),
                new LinkedHashSet<>(Arrays.asList(MeasureType.EVENTS_QUANTITY, MeasureType.DURATION,
                        MeasureType.TALKS_QUANTITY, MeasureType.SPEAKERS_QUANTITY, MeasureType.JAVA_CHAMPIONS_QUANTITY,
                        MeasureType.MVPS_QUANTITY)));
        Cube speakersCube = new Cube(
                new LinkedHashSet<>(Arrays.asList(DimensionType.EVENT_TYPE, DimensionType.SPEAKER, DimensionType.YEAR)),
                new LinkedHashSet<>(Arrays.asList(MeasureType.TALKS_QUANTITY, MeasureType.EVENTS_QUANTITY,
                        MeasureType.EVENT_TYPES_QUANTITY)));
        Cube companiesCube = new Cube(
                new LinkedHashSet<>(Arrays.asList(DimensionType.EVENT_TYPE, DimensionType.COMPANY, DimensionType.SPEAKER,
                        DimensionType.YEAR)),
                new LinkedHashSet<>(Arrays.asList(MeasureType.TALKS_QUANTITY, MeasureType.EVENTS_QUANTITY,
                        MeasureType.EVENT_TYPES_QUANTITY, MeasureType.SPEAKERS_QUANTITY, MeasureType.JAVA_CHAMPIONS_QUANTITY,
                        MeasureType.MVPS_QUANTITY)));

        cubes.put(CubeType.EVENT_TYPES, eventTypesCube);
        cubes.put(CubeType.SPEAKERS, speakersCube);
        cubes.put(CubeType.COMPANIES, companiesCube);

        fillDimensions(eventTypesCube, speakersCube, companiesCube);
        fillMeasures(eventTypesCube, speakersCube, companiesCube);
    }

    @Override
    public Cube getCube(CubeType cubeType) {
        Cube cube = cubes.get(cubeType);

        return Objects.requireNonNull(cube, () -> String.format("Cube type %s not found", cubeType));
    }

    @Override
    public List<MeasureType> getMeasureTypes(CubeType cubeType) {
        return List.copyOf(getCube(cubeType).getMeasureTypes());
    }

    private void fillDimensions(Cube eventTypesCube, Cube speakersCube, Cube companiesCube) {
        // Event type dimension values
        Set<EventType> eventTypes = new HashSet<>(eventTypeDao.getEventTypes());

        // Speaker dimension values
        Set<Speaker> speakers = eventTypeDao.getEventTypes().stream()
                .flatMap(et -> et.getEvents().stream())
                .flatMap(e -> e.getTalks().stream())
                .flatMap(t -> t.getSpeakers().stream())
                .collect(Collectors.toSet());
        Set<Speaker> companySpeakers = speakers.stream()
                .filter(s -> !s.getCompanies().isEmpty())
                .collect(Collectors.toSet());

        // Company dimension values
        Set<Company> companies = eventTypeDao.getEventTypes().stream()
                .flatMap(et -> et.getEvents().stream())
                .flatMap(e -> e.getTalks().stream())
                .flatMap(t -> t.getSpeakers().stream())
                .flatMap(t -> t.getCompanies().stream())
                .collect(Collectors.toSet());

        // Year dimension values
        IntSummaryStatistics summaryStatistics = eventTypeDao.getEventTypes().stream()
                .flatMap(et -> et.getEvents().stream())
                .filter(e -> e.getStartDate() != null)
                .map(e -> e.getStartDate().getYear())
                .mapToInt(y -> y)
                .summaryStatistics();
        Set<Integer> years = IntStream.rangeClosed(summaryStatistics.getMin(), summaryStatistics.getMax())
                .boxed()
                .collect(Collectors.toSet());

        eventTypesCube.addDimensions(DimensionType.EVENT_TYPE, eventTypes);
        eventTypesCube.addDimensions(DimensionType.YEAR, years);

        speakersCube.addDimensions(DimensionType.EVENT_TYPE, eventTypes);
        speakersCube.addDimensions(DimensionType.SPEAKER, speakers);
        speakersCube.addDimensions(DimensionType.YEAR, years);

        companiesCube.addDimensions(DimensionType.EVENT_TYPE, eventTypes);
        companiesCube.addDimensions(DimensionType.COMPANY, companies);
        companiesCube.addDimensions(DimensionType.SPEAKER, companySpeakers);
        companiesCube.addDimensions(DimensionType.YEAR, years);
    }

    private void fillMeasures(Cube eventTypesCube, Cube speakersCube, Cube companiesCube) {
        List<EventType> eventTypes = eventTypeDao.getEventTypes();

        for (EventType eventType : eventTypes) {
            // Event type dimension
            EventTypeDimension eventTypeDimension = new EventTypeDimension(eventType);

            for (Event event : eventType.getEvents()) {
                // Year dimension
                YearDimension yearDimension = new YearDimension(event.getStartDate().getYear());

                // Event type and year dimensions
                Set<Dimension<?>> eventTypeAndYearDimensions = Set.of(eventTypeDimension, yearDimension);

                // Event measure values
                eventTypesCube.addMeasureEntity(eventTypeAndYearDimensions, MeasureType.DURATION, event);
                eventTypesCube.addMeasureEntity(eventTypeAndYearDimensions, MeasureType.EVENTS_QUANTITY, event);

                for (Talk talk : event.getTalks()) {
                    // Talk measure values
                    eventTypesCube.addMeasureEntity(eventTypeAndYearDimensions, MeasureType.TALKS_QUANTITY, talk);

                    for (Speaker speaker : talk.getSpeakers()) {
                        // Speaker dimension
                        SpeakerDimension speakerDimension = new SpeakerDimension(speaker);

                        // Event type, speaker and year dimension
                        Set<Dimension<?>> eventTypeAndSpeakerAndYearDimensions = Set.of(
                                eventTypeDimension, speakerDimension, yearDimension);

                        // Speaker measure values
                        eventTypesCube.addMeasureEntity(eventTypeAndYearDimensions, MeasureType.SPEAKERS_QUANTITY, speaker);

                        speakersCube.addMeasureEntity(eventTypeAndSpeakerAndYearDimensions, MeasureType.TALKS_QUANTITY, talk);
                        speakersCube.addMeasureEntity(eventTypeAndSpeakerAndYearDimensions, MeasureType.EVENTS_QUANTITY, event);
                        speakersCube.addMeasureEntity(eventTypeAndSpeakerAndYearDimensions, MeasureType.EVENT_TYPES_QUANTITY, eventType);

                        if (speaker.isJavaChampion()) {
                            eventTypesCube.addMeasureEntity(eventTypeAndYearDimensions, MeasureType.JAVA_CHAMPIONS_QUANTITY, speaker);
                        }

                        if (speaker.isAnyMvp()) {
                            eventTypesCube.addMeasureEntity(eventTypeAndYearDimensions, MeasureType.MVPS_QUANTITY, speaker);
                        }

                        for (Company company : speaker.getCompanies()) {
                            // Event type, company, speaker and year dimension
                            Set<Dimension<?>> eventTypeAndCompanyAndSpeakerAndYearDimensions = Set.of(
                                    eventTypeDimension, new CompanyDimension(company), speakerDimension, yearDimension);

                            // Company measure values
                            companiesCube.addMeasureEntity(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.SPEAKERS_QUANTITY, speaker);
                            companiesCube.addMeasureEntity(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.TALKS_QUANTITY, talk);
                            companiesCube.addMeasureEntity(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.EVENTS_QUANTITY, event);
                            companiesCube.addMeasureEntity(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.EVENT_TYPES_QUANTITY, eventType);

                            if (speaker.isJavaChampion()) {
                                companiesCube.addMeasureEntity(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.JAVA_CHAMPIONS_QUANTITY, speaker);
                            }

                            if (speaker.isAnyMvp()) {
                                companiesCube.addMeasureEntity(eventTypeAndCompanyAndSpeakerAndYearDimensions, MeasureType.MVPS_QUANTITY, speaker);
                            }
                        }
                    }
                }
            }
        }
    }
}
