package guess.domain.statistics.olap;

import java.util.List;

/**
 * Cube.
 */
public class Cube {
    private final List<DimensionType> dimensionTypes;
    private final List<MeasureType> measureTypes;

    public Cube(List<DimensionType> dimensionTypes, List<MeasureType> measureTypes) {
        this.dimensionTypes = dimensionTypes;
        this.measureTypes = measureTypes;
    }

    public List<DimensionType> getDimensionTypes() {
        return dimensionTypes;
    }

    public List<MeasureType> getMeasureTypes() {
        return measureTypes;
    }
}
