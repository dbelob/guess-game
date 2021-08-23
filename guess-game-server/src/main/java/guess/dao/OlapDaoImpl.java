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
                new LinkedHashSet<>(Arrays.asList(MeasureType.DURATION, MeasureType.EVENTS_QUANTITY,
                        MeasureType.TALKS_QUANTITY, MeasureType.SPEAKERS_QUANTITY,
                        MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY)));
        Cube speakersCube = new Cube(
                new LinkedHashSet<>(Arrays.asList(DimensionType.EVENT_TYPE, DimensionType.SPEAKER, DimensionType.YEAR)),
                new LinkedHashSet<>(Arrays.asList(MeasureType.TALKS_QUANTITY, MeasureType.EVENTS_QUANTITY,
                        MeasureType.EVENT_TYPES_QUANTITY, MeasureType.JAVA_CHAMPIONS_QUANTITY,
                        MeasureType.MVPS_QUANTITY)));
        Cube companiesCube = new Cube(
                new LinkedHashSet<>(Arrays.asList(DimensionType.EVENT_TYPE, DimensionType.COMPANY, DimensionType.YEAR)),
                new LinkedHashSet<>(Arrays.asList(MeasureType.SPEAKERS_QUANTITY, MeasureType.TALKS_QUANTITY,
                        MeasureType.EVENTS_QUANTITY, MeasureType.EVENT_TYPES_QUANTITY,
                        MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY)));

        cubes.put(CubeType.EVENT_TYPES, eventTypesCube);
        cubes.put(CubeType.SPEAKERS, speakersCube);
        cubes.put(CubeType.COMPANIES, companiesCube);

        fillDimensions(eventTypesCube, speakersCube, companiesCube);
        fillMeasures(eventTypesCube, speakersCube, companiesCube);
    }

    @Override
    public List<MeasureType> getMeasureTypes(CubeType cubeType) {
        Cube cube = cubes.get(cubeType);

        return List.copyOf(Objects.requireNonNull(cube, () -> String.format("Cube type %s not found", cubeType))
                .getMeasureTypes());
    }

    private void fillDimensions(Cube eventTypesCube, Cube speakersCube, Cube companiesCube) {
        //TODO: implement
    }

    private void fillMeasures(Cube eventTypesCube, Cube speakersCube, Cube companiesCube) {
        List<EventType> eventTypes = eventTypeDao.getEventTypes();

        for (EventType eventType : eventTypes) {
            // Event type dimension
            Dimension eventTypeDimension = new EventTypeDimension(eventType);

            for (Event event : eventType.getEvents()) {
                // Year dimension
                Dimension yearDimension = new YearDimension(event.getStartDate().getYear());

                // Event type and year dimensions
                Set<Dimension> eventTypeAndYearDimensions = Set.of(eventTypeDimension, yearDimension);

                // Event measure values
                eventTypesCube.addMeasureEntity(eventTypeAndYearDimensions, MeasureType.DURATION, event);
                eventTypesCube.addMeasureEntity(eventTypeAndYearDimensions, MeasureType.EVENTS_QUANTITY, event);

                for (Talk talk : event.getTalks()) {
                    // Talk measure values
                    eventTypesCube.addMeasureEntity(eventTypeAndYearDimensions, MeasureType.TALKS_QUANTITY, talk);

                    for (Speaker speaker : talk.getSpeakers()) {
                        // Event type, speaker and year dimension
                        Set<Dimension> eventTypeAndSpeakerAndYearDimensions = Set.of(
                                eventTypeDimension, new SpeakerDimension(speaker), yearDimension);

                        // Speaker measure values
                        eventTypesCube.addMeasureEntity(eventTypeAndYearDimensions, MeasureType.SPEAKERS_QUANTITY, speaker);

                        speakersCube.addMeasureEntity(eventTypeAndSpeakerAndYearDimensions, MeasureType.TALKS_QUANTITY, talk);
                        speakersCube.addMeasureEntity(eventTypeAndSpeakerAndYearDimensions, MeasureType.EVENTS_QUANTITY, event);
                        speakersCube.addMeasureEntity(eventTypeAndSpeakerAndYearDimensions, MeasureType.EVENT_TYPES_QUANTITY, eventType);

                        if (speaker.isJavaChampion()) {
                            eventTypesCube.addMeasureEntity(eventTypeAndYearDimensions, MeasureType.JAVA_CHAMPIONS_QUANTITY, speaker);
                            speakersCube.addMeasureEntity(eventTypeAndSpeakerAndYearDimensions, MeasureType.JAVA_CHAMPIONS_QUANTITY, speaker);
                        }

                        if (speaker.isAnyMvp()) {
                            eventTypesCube.addMeasureEntity(eventTypeAndYearDimensions, MeasureType.MVPS_QUANTITY, speaker);
                            speakersCube.addMeasureEntity(eventTypeAndSpeakerAndYearDimensions, MeasureType.MVPS_QUANTITY, speaker);
                        }

                        for (Company company : speaker.getCompanies()) {
                            // Event type, company and year dimension
                            Set<Dimension> eventTypeAndCompanyAndYearDimensions = Set.of(
                                    eventTypeDimension, new CompanyDimension(company), yearDimension);

                            // Company measure values
                            companiesCube.addMeasureEntity(eventTypeAndCompanyAndYearDimensions, MeasureType.SPEAKERS_QUANTITY, speaker);
                            companiesCube.addMeasureEntity(eventTypeAndCompanyAndYearDimensions, MeasureType.TALKS_QUANTITY, talk);
                            companiesCube.addMeasureEntity(eventTypeAndCompanyAndYearDimensions, MeasureType.EVENTS_QUANTITY, event);
                            companiesCube.addMeasureEntity(eventTypeAndCompanyAndYearDimensions, MeasureType.EVENT_TYPES_QUANTITY, eventType);

                            if (speaker.isJavaChampion()) {
                                companiesCube.addMeasureEntity(eventTypeAndCompanyAndYearDimensions, MeasureType.JAVA_CHAMPIONS_QUANTITY, speaker);
                            }

                            if (speaker.isAnyMvp()) {
                                companiesCube.addMeasureEntity(eventTypeAndCompanyAndYearDimensions, MeasureType.MVPS_QUANTITY, speaker);
                            }
                        }
                    }
                }
            }
        }
    }
}
