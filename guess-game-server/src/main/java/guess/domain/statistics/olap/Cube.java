package guess.domain.statistics.olap;

import guess.domain.statistics.olap.dimension.Dimension;
import guess.domain.statistics.olap.dimension.DimensionFactory;
import guess.domain.statistics.olap.measure.Measure;
import guess.domain.statistics.olap.measure.MeasureFactory;

import java.util.*;
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

    public List<Object> getDimensionValues(DimensionType dimensionType) {
        checkDimensionType(dimensionType);

        return dimensionMap.get(dimensionType).stream()
                .map(Dimension::getValue)
                .collect(Collectors.toList());
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

    private DimensionType getDimensionTypeByDimension(Dimension<?> dimension) {
        for (DimensionType dimensionType : dimensionTypes) {
            Set<Dimension<?>> dimensionSet = dimensionMap.get(dimensionType);

            if ((dimensionSet != null) && dimensionSet.contains(dimension)) {
                return dimensionType;
            }
        }

        return null;
    }

    private void checkDimensionSet(Set<Dimension<?>> dimensions) {
        // Check dimension set size
        if (dimensionTypes.size() != dimensions.size()) {
            throw new IllegalArgumentException(String.format("Invalid size of dimension set (%d), valid size is %d", dimensions.size(), dimensionTypes.size()));
        }

        Set<DimensionType> foundDimensionTypes = new HashSet<>();

        // Check dimensions existence
        dimensions.forEach(d -> {
            DimensionType foundDimensionType = getDimensionTypeByDimension(d);

            if (foundDimensionType == null) {
                throw new IllegalArgumentException("Dimension not found in cube");
            }

            foundDimensionTypes.add(foundDimensionType);
        });

        // Check the number of dimension types for found dimensions
        if (foundDimensionTypes.size() != dimensions.size()) {
            throw new IllegalArgumentException(String.format("Invalid size of found dimension set (%d), valid size is %d", dimensions.size(), dimensionTypes.size()));
        }
    }

    public void addDimensions(DimensionType dimensionType, Set<?> values) {
        checkDimensionType(dimensionType);

        Set<Dimension<?>> dimensions = values.stream()
                .map(v -> (Dimension<?>) DimensionFactory.create(dimensionType, v))
                .collect(Collectors.toSet());

        this.dimensionMap.put(dimensionType, dimensions);
    }

    public void addMeasureEntity(Set<Dimension<?>> dimensions, MeasureType measureType, Object entity) {
        checkDimensionSet(dimensions);
        checkMeasureType(measureType);

        Map<MeasureType, Measure<?>> dimensionMeasures = measureMap.computeIfAbsent(dimensions, k -> new HashMap<>());
        Measure<?> measure = dimensionMeasures.get(measureType);

        if (measure == null) {
            dimensionMeasures.put(measureType, MeasureFactory.create(measureType, Set.of(entity)));
        } else {
            measure.addEntity(entity);
        }
    }

    public Long getMeasureValue(Set<Dimension<?>> dimensions, MeasureType measureType) {
        List<Measure<?>> measures = measureMap.entrySet().stream()
                .filter(e -> e.getKey().containsAll(dimensions))
                .map(Map.Entry::getValue)
                .filter(e -> e.containsKey(measureType))
                .map(e -> e.get(measureType))
                .collect(Collectors.toList());

        if (measures.isEmpty()) {
            return 0L;
        } else if (measures.size() == 1) {
            return measures.get(0).calculateValue();
        } else {
            Set<Object> measureValues = measures.stream()
                    .flatMap(m -> m.getEntities().stream())
                    .collect(Collectors.toSet());
            Measure<?> measure = MeasureFactory.create(measureType, measureValues);

            return measure.calculateValue();
        }
    }
}
