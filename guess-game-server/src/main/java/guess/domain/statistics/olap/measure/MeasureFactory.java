package guess.domain.statistics.olap.measure;

import guess.domain.statistics.olap.MeasureType;

/**
 * Measure factory.
 */
public class MeasureFactory {
    private MeasureFactory() {
    }

    @SuppressWarnings("rawtypes")
    public static Measure create(MeasureType measureType, Object entity) {
        switch (measureType) {
            case DURATION:
                return new DurationMeasure(entity);
            case EVENT_TYPES_QUANTITY:
                return new EventTypesQuantityMeasure(entity);
            case EVENTS_QUANTITY:
                return new EventsQuantityMeasure(entity);
            case TALKS_QUANTITY:
                return new TalksQuantityMeasure(entity);
            case SPEAKERS_QUANTITY:
                return new SpeakersQuantityMeasure(entity);
            case JAVA_CHAMPIONS_QUANTITY:
                return new JavaChampionsQuantityMeasure(entity);
            case MVPS_QUANTITY:
                return new MvpsQuantityMeasure(entity);
            default:
                throw new IllegalStateException(String.format("Unknown measure type %s", measureType));
        }
    }
}
