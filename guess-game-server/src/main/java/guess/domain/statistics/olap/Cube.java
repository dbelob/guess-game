package guess.domain.statistics.olap;

import guess.domain.statistics.olap.dimension.Dimension;
import guess.domain.statistics.olap.dimension.DimensionFactory;
import guess.domain.statistics.olap.measure.Measure;
import guess.domain.statistics.olap.measure.MeasureFactory;
import org.apache.commons.lang3.function.TriFunction;

import java.util.*;
import java.util.function.BiFunction;
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

    public Long getMeasureValue(List<Measure<?>> measures, MeasureType measureType) {
        if ((measures == null) || measures.isEmpty()) {
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T, S, U, V, W, Y> Y getMeasureValueEntities(DimensionTypeValues<T> firstDimensionTypeValues,
                                                        DimensionTypeValues<S> secondDimensionTypeValues,
                                                        DimensionTypeValues<U> filterDimensionTypeValues,
                                                        MeasureType measureType, TriFunction<T, List<Long>, Long, V> entityTriFunction,
                                                        BiFunction<List<Long>, Long, W> totalsBiFunction,
                                                        TriFunction<List<S>, List<V>, W, Y> resultTriFunction) {
        Set<Dimension> firstDimensions = firstDimensionTypeValues.getValues().stream()
                .map(v -> DimensionFactory.create(firstDimensionTypeValues.getType(), v))
                .collect(Collectors.toSet());
        Set<Dimension> secondDimensions = secondDimensionTypeValues.getValues().stream()
                .map(v -> DimensionFactory.create(secondDimensionTypeValues.getType(), v))
                .collect(Collectors.toSet());
        Set<Dimension> filterDimensions = filterDimensionTypeValues.getValues().stream()
                .map(v -> DimensionFactory.create(filterDimensionTypeValues.getType(), v))
                .collect(Collectors.toSet());
        Map<T, Map<S, List<Measure<?>>>> measuresByFirstDimensionValue = new HashMap<>();
        Map<T, List<Measure<?>>> firstDimensionTotalMeasures = new HashMap<>();
        Map<S, List<Measure<?>>> secondDimensionTotalMeasures = new HashMap<>();

        // Create intermediate measure map
        for (Map.Entry<Set<Dimension<?>>, Map<MeasureType, Measure<?>>> entry : measureMap.entrySet()) {
            Set<Dimension<?>> entryDimensions = entry.getKey();

            // Search first dimension value
            for (Dimension<?> firstEntryDimension : entryDimensions) {
                if (firstDimensions.contains(firstEntryDimension)) {
                    T firstDimensionValue = (T) firstEntryDimension.getValue();
                    Map<S, List<Measure<?>>> measuresBySecondDimensionValue = measuresByFirstDimensionValue.computeIfAbsent(firstDimensionValue, k -> new HashMap<>());

                    // Search second dimension value
                    for (Dimension<?> secondEntryDimension : entryDimensions) {
                        if (secondDimensions.contains(secondEntryDimension)) {

                            // Filter by values of third dimension
                            for (Dimension<?> thirdEntryDimension : entryDimensions) {
                                if (filterDimensions.contains(thirdEntryDimension)) {
                                    Measure<?> measure = entry.getValue().get(measureType);

                                    if (measure != null) {
                                        S secondDimensionValue = (S) secondEntryDimension.getValue();

                                        // Measures of first and second dimension
                                        measuresBySecondDimensionValue
                                                .computeIfAbsent(secondDimensionValue, k -> new ArrayList<>())
                                                .add(measure);

                                        // Measures for total of first dimension
                                        firstDimensionTotalMeasures
                                                .computeIfAbsent(firstDimensionValue, k -> new ArrayList<>())
                                                .add(measure);

                                        // Measures for total of second dimension
                                        secondDimensionTotalMeasures
                                                .computeIfAbsent(secondDimensionValue, k -> new ArrayList<>())
                                                .add(measure);
                                    }

                                    break;
                                }
                            }

                            break;
                        }
                    }

                    break;
                }
            }
        }

        // Fill resulting list
        List<V> measureValueEntities = new ArrayList<>();

        for (T firstDimensionValue : firstDimensionTypeValues.getValues()) {
            Map<S, List<Measure<?>>> measuresBySecondDimensionValue = measuresByFirstDimensionValue.get(firstDimensionValue);
            List<Long> measureValues;

            if (measuresBySecondDimensionValue == null) {
                measureValues = Collections.nCopies(secondDimensionTypeValues.getValues().size(), 0L);
            } else {
                measureValues = new ArrayList<>();

                for (S secondDimensionValue : secondDimensionTypeValues.getValues()) {
                    List<Measure<?>> measures = measuresBySecondDimensionValue.get(secondDimensionValue);
                    measureValues.add(getMeasureValue(measures, measureType));
                }
            }

            List<Measure<?>> measures = firstDimensionTotalMeasures.get(firstDimensionValue);
            Long total = getMeasureValue(measures, measureType);

            measureValueEntities.add(entityTriFunction.apply(firstDimensionValue, measureValues, total));
        }

        // Fill totals
        List<Long> totals = new ArrayList<>();
        List<Measure<?>> allTotalMeasures = new ArrayList<>();

        for (S secondDimensionValue : secondDimensionTypeValues.getValues()) {
            List<Measure<?>> measures = secondDimensionTotalMeasures.get(secondDimensionValue);
            Long total = getMeasureValue(measures, measureType);

            totals.add(total);

            if (measures != null) {
                allTotalMeasures.addAll(measures);
            }
        }

        // Fill all total
        Long allTotal = getMeasureValue(allTotalMeasures, measureType);

        return resultTriFunction.apply(
                secondDimensionTypeValues.getValues(),
                measureValueEntities,
                totalsBiFunction.apply(totals, allTotal));
    }
}
