package guess.domain.statistics.olap;

import guess.domain.statistics.olap.dimension.Dimension;
import guess.domain.statistics.olap.dimension.DimensionFactory;
import guess.domain.statistics.olap.measure.Measure;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Cube.
 */
public class Cube {
    private final Set<DimensionType> dimensionTypes;
    private final Set<MeasureType> measureTypes;
    private final Map<DimensionType, Set<Dimension<?>>> dimensionMap = new EnumMap<>(DimensionType.class);
    private final Map<Set<Dimension<?>>, Map<MeasureType, Measure<?>>> measureMap = new HashMap<>();

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

    private void checkDimensionType(DimensionType dimensionType) {
        if (!dimensionTypes.contains(dimensionType)) {
            throw new IllegalArgumentException(String.format("Invalid dimension type %s for %s valid values", dimensionType, dimensionTypes));
        }
    }

    private void checkMeasureType(MeasureType measureType) {
        if (!measureTypes.contains(measureType)) {
            throw new IllegalArgumentException(String.format("Invalid measure type %s for %s valid values", measureType, measureTypes));
        }
    }

    private boolean dimensionContains(Dimension<?> dimension) {
        for (DimensionType dimensionType : dimensionTypes) {
            Set<Dimension<?>> dimensionSet = dimensionMap.get(dimensionType);

            if (dimensionSet != null) {
                if (dimensionSet.contains(dimension)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void checkDimensionSet(Set<Dimension<?>> dimensions) {
        dimensions.forEach(d -> {
            if (!dimensionContains(d)) {
                throw new IllegalArgumentException("Dimension not found in cube");
            }
        });
    }

    public void addDimensions(DimensionType dimensionType, Set<?> dimensionValues) {
        checkDimensionType(dimensionType);

        Set<Dimension<?>> dimensions = dimensionValues.stream()
                .map(v -> (Dimension<?>) DimensionFactory.create(dimensionType, v))
                .collect(Collectors.toSet());

        this.dimensionMap.put(dimensionType, dimensions);
    }

    public void addMeasureEntity(Set<Dimension<?>> dimensions, MeasureType measureType, Object value) {
        checkDimensionSet(dimensions);
        checkMeasureType(measureType);

//        Map<MeasureType, Measure<?>> dimensionMeasures = measureMap.computeIfAbsent(dimensions, k -> new HashMap<>());
//        dimensionMeasures.put(measureType, measure);

        //TODO: implement
    }
}
