package guess.domain.statistics.olap;

import guess.domain.statistics.olap.dimension.Dimension;
import guess.domain.statistics.olap.measure.Measure;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Cube.
 */
public class Cube {
    private final Set<DimensionType> dimensionTypes;
    private final Set<MeasureType> measureTypes;
    private final Map<DimensionType, Set<Dimension>> dimensionMap = new EnumMap<>(DimensionType.class);
    private final Set<Measure> measures = new HashSet<>();

    public Cube(Set<DimensionType> dimensionTypes, Set<MeasureType> measureTypes) {
        this.dimensionTypes = dimensionTypes;
        this.measureTypes = measureTypes;
    }

    public Set<DimensionType> getDimensionTypes() {
        return dimensionTypes;
    }

    public Set<MeasureType> getMeasureTypes() {
        return measureTypes;
    }

    public void addDimensions(DimensionType dimensionType, Set<Dimension> dimensions) {
        if (!dimensionTypes.contains(dimensionType)) {
            //TODO: throw exception
        }

        this.dimensionMap.put(dimensionType, dimensions);
    }

    public void addMeasure(Measure measure) {
        //TODO: check dimension type existence for each dimension

        //TODO: check dimension existence for each dimension

        measures.add(measure);
    }

    public void addMeasureValue(Set<Dimension> dimensions, MeasureType measureType, Object value) {
        //TODO: implement
    }
}
