package guess.domain.statistics.olap.dimension;

import guess.domain.statistics.olap.DimensionType;

/**
 * Dimension factory.
 */
public class DimensionFactory {
    private DimensionFactory() {
    }

    @SuppressWarnings("rawtypes")
    public static Dimension create(DimensionType dimensionType, Object value) {
        switch (dimensionType) {
            case EVENT_TYPE:
                return new EventTypeDimension(value);
            case SPEAKER:
                return new SpeakerDimension(value);
            case COMPANY:
                return new CompanyDimension(value);
            case YEAR:
                return new YearDimension(value);
            default:
                throw new IllegalStateException(String.format("Unknown dimension type %s", dimensionType));
        }
    }
}
