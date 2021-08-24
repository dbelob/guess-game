package guess.domain.statistics.olap;

import guess.domain.statistics.olap.dimension.Dimension;
import guess.domain.statistics.olap.measure.Measure;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Cube.
 */
public class Cube {
    private final Set<DimensionType> dimensionTypes;
    private final Set<MeasureType> measureTypes;
    private final Map<DimensionType, Set<Dimension<?>>> dimensionMap = new EnumMap<>(DimensionType.class);
    private final Map<Set<Dimension<?>>, Map<MeasureType, Measure>> measureMap = new HashMap<>();

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

    public void addDimensions(DimensionType dimensionType, Set<Dimension<?>> dimensions) {
        if (!dimensionTypes.contains(dimensionType)) {
            throw new IllegalStateException(String.format("Invalid dimension type %s for %s valid values", dimensionType, dimensionTypes));
        }

        dimensions.forEach(d -> {
            if (!dimensionType.isDimensionValid(d)) {
                throw new IllegalStateException(String.format("Invalid dimension %s, valid dimension is %s ", d.getClass().getSimpleName(), dimensionType.getDimensionClass().getSimpleName()));
            }
        });

        this.dimensionMap.put(dimensionType, dimensions);
    }

    public void addMeasure(Set<Dimension<?>> dimensions, MeasureType measureType, Measure measure) {
        //TODO: check dimension type existence for each dimension

        //TODO: check dimension existence for each dimension

        Map<MeasureType, Measure> dimensionMeasures = measureMap.computeIfAbsent(dimensions, k -> new HashMap<>());

        dimensionMeasures.put(measureType, measure);
    }

    public void addMeasureEntity(Set<Dimension<?>> dimensions, MeasureType measureType, Object value) {
        //TODO: implement
    }
}
