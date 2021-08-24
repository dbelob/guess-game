package guess.domain.statistics.olap;

import guess.domain.statistics.olap.dimension.*;

/**
 * Dimension type.
 */
public enum DimensionType {
    EVENT_TYPE(EventTypeDimension.class),
    SPEAKER(SpeakerDimension.class),
    COMPANY(CompanyDimension.class),
    YEAR(YearDimension.class);

    private final Class<? extends Dimension<?>> dimensionClass;

    DimensionType(Class<? extends Dimension<?>> dimensionClass) {
        this.dimensionClass = dimensionClass;
    }

    public Class<? extends Dimension<?>> getDimensionClass() {
        return dimensionClass;
    }

    public boolean isDimensionValid(Dimension<?> dimension) {
        return dimensionClass.isInstance(dimension);
    }
}
