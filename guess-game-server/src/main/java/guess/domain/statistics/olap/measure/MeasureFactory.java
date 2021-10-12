package guess.domain.statistics.olap.measure;

import guess.domain.statistics.olap.MeasureType;

import java.util.Set;

/**
 * Measure factory.
 */
public class MeasureFactory {
    private MeasureFactory() {
    }

    @SuppressWarnings("rawtypes")
    public static Measure create(MeasureType measureType, Set<Object> entities) {
        switch (measureType) {
            case DURATION:
                return new DurationMeasure(entities);
            case EVENT_TYPES_QUANTITY:
                return new EventTypesQuantityMeasure(entities);
            case EVENTS_QUANTITY:
                return new EventsQuantityMeasure(entities);
            case TALKS_QUANTITY:
                return new TalksQuantityMeasure(entities);
            case SPEAKERS_QUANTITY:
                return new SpeakersQuantityMeasure(entities);
            case JAVA_CHAMPIONS_QUANTITY:
                return new JavaChampionsQuantityMeasure(entities);
            case MVPS_QUANTITY:
                return new MvpsQuantityMeasure(entities);
            default:
                throw new IllegalStateException(String.format("Unknown measure type %s", measureType));
        }
    }
}
