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
 * OLAP cube.
 */
public class Cube {
    private static class MeasureMaps<T, S> {
        private final Map<T, List<Measure<?>>> firstDimensionTotalMeasures;
        private final Map<S, List<Measure<?>>> secondDimensionTotalMeasures;
        private final Map<S, List<Measure<?>>> measuresBySecondDimensionValue;

        public MeasureMaps(Map<T, List<Measure<?>>> firstDimensionTotalMeasures,
                           Map<S, List<Measure<?>>> secondDimensionTotalMeasures,
                           Map<S, List<Measure<?>>> measuresBySecondDimensionValue) {
            this.firstDimensionTotalMeasures = firstDimensionTotalMeasures;
            this.secondDimensionTotalMeasures = secondDimensionTotalMeasures;
            this.measuresBySecondDimensionValue = measuresBySecondDimensionValue;
        }
    }

    private final Set<DimensionType> dimensionTypes;
    private final Set<MeasureType> measureTypes;
    private final Map<DimensionType, Set<Dimension<?>>> dimensionMap = new EnumMap<>(DimensionType.class);
    private final Map<Set<Dimension<?>>, Map<MeasureType, Measure<?>>> measureMap = new HashMap<>();

    public Cube(Set<DimensionType> dimensionTypes, Set<MeasureType> measureTypes) {
        this.dimensionTypes = dimensionTypes;
        this.measureTypes = measureTypes;
    }

    /**
     * Gets dimension types.
     *
     * @return dimension types
     */
    public Set<DimensionType> getDimensionTypes() {
        return dimensionTypes;
    }

    /**
     * Gets measure types.
     *
     * @return measure types
     */
    public Set<MeasureType> getMeasureTypes() {
        return measureTypes;
    }

    /**
     * Gets dimension values.
     *
     * @param dimensionType dimension type
     * @return dimension values
     */
    public List<Object> getDimensionValues(DimensionType dimensionType) {
        checkDimensionType(dimensionType);

        return dimensionMap.getOrDefault(dimensionType, Collections.emptySet()).stream()
                .map(v -> (Object) v.getValue())
                .toList();
    }

    /**
     * Checks the existence of dimension type.
     *
     * @param dimensionType dimension type
     */
    private void checkDimensionType(DimensionType dimensionType) {
        if (!dimensionTypes.contains(dimensionType)) {
            throw new IllegalArgumentException(String.format("Invalid dimension type %s for %s valid values", dimensionType, dimensionTypes));
        }
    }

    /**
     * Checks the existence of measure type.
     *
     * @param measureType measure type
     */
    private void checkMeasureType(MeasureType measureType) {
        if (!measureTypes.contains(measureType)) {
            throw new IllegalArgumentException(String.format("Invalid measure type %s for %s valid values", measureType, measureTypes));
        }
    }

    /**
     * Gets dimension type by dimension.
     *
     * @param dimension dimension
     * @return dimension type
     */
    private DimensionType getDimensionTypeByDimension(Dimension<?> dimension) {
        for (DimensionType dimensionType : dimensionTypes) {
            Set<Dimension<?>> dimensionSet = dimensionMap.get(dimensionType);

            if ((dimensionSet != null) && dimensionSet.contains(dimension)) {
                return dimensionType;
            }
        }

        return null;
    }

    /**
     * Checks dimension set.
     *
     * @param dimensions dimension set
     */
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

    /**
     * Adds dimensions.
     *
     * @param dimensionType dimension type
     * @param values        dimension values
     */
    public void addDimensions(DimensionType dimensionType, Set<?> values) {
        checkDimensionType(dimensionType);

        Set<Dimension<?>> dimensions = values.stream()
                .map(v -> (Dimension<?>) DimensionFactory.create(dimensionType, v))
                .collect(Collectors.toSet());

        this.dimensionMap.put(dimensionType, dimensions);
    }

    /**
     * Adds measure entity.
     *
     * @param dimensions  dimension set
     * @param measureType measure type
     * @param entity      entity
     */
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

    /**
     * Gets measure value by measures.
     *
     * @param measures    measure list
     * @param measureType measure type
     * @return measure value
     */
    public long getMeasureValue(List<? extends Measure<?>> measures, MeasureType measureType) {
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

    /**
     * Gets measure value by dimensions.
     * <p>
     * Slow, only used in unit tests.
     *
     * @param dimensions  dimension set
     * @param measureType measure type
     * @return measure value
     */
    public long getMeasureValue(Set<Dimension<?>> dimensions, MeasureType measureType) {
        List<? extends Measure<?>> measures = measureMap.entrySet().stream()
                .filter(e -> e.getKey().containsAll(dimensions))
                .map(Map.Entry::getValue)
                .filter(e -> e.containsKey(measureType))
                .map(e -> e.get(measureType))
                .toList();

        return getMeasureValue(measures, measureType);
    }

    /**
     * Gets measure value entities.
     * <p>
     * Fast, used in the application.
     *
     * @param firstDimensionTypeValues  values of first dimension type
     * @param secondDimensionTypeValues values of second dimension type
     * @param filterDimensionTypeValues values of filter dimension type
     * @param measureType               measure type
     * @param entityTriFunction         result element function
     * @param totalsBiFunction          totals function
     * @param resultTriFunction         result function
     * @param <T>                       first dimension type
     * @param <S>                       second dimension type
     * @param <U>                       filter dimension type
     * @param <V>                       result element type
     * @param <W>                       totals type
     * @param <Y>                       result type
     * @return measure value entities
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T, S, U, V, W, Y> Y getMeasureValueEntities(DimensionTypeValues<T> firstDimensionTypeValues,
                                                        DimensionTypeValues<S> secondDimensionTypeValues,
                                                        DimensionTypeValues<U> filterDimensionTypeValues,
                                                        MeasureType measureType,
                                                        TriFunction<T, List<Long>, Long, V> entityTriFunction,
                                                        BiFunction<List<Long>, Long, W> totalsBiFunction,
                                                        TriFunction<List<S>, List<V>, W, Y> resultTriFunction) {
        Set<Dimension> firstDimensions = firstDimensionTypeValues.values().stream()
                .map(v -> DimensionFactory.create(firstDimensionTypeValues.type(), v))
                .collect(Collectors.toSet());
        Set<Dimension> secondDimensions = secondDimensionTypeValues.values().stream()
                .map(v -> DimensionFactory.create(secondDimensionTypeValues.type(), v))
                .collect(Collectors.toSet());
        Set<Dimension> filterDimensions = filterDimensionTypeValues.values().stream()
                .map(v -> DimensionFactory.create(filterDimensionTypeValues.type(), v))
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
                            MeasureMaps<T, S> measureMaps = new MeasureMaps<>(firstDimensionTotalMeasures, secondDimensionTotalMeasures, measuresBySecondDimensionValue);
                            filterByThirdDimensionValues(measureType, filterDimensions, measureMaps, entry, firstDimensionValue, secondEntryDimension);

                            break;
                        }
                    }

                    break;
                }
            }
        }

        // Fill resulting list
        List<V> measureValueEntities = new ArrayList<>();

        fillResultingList(firstDimensionTypeValues, secondDimensionTypeValues, measureType, entityTriFunction,
                measuresByFirstDimensionValue, firstDimensionTotalMeasures, measureValueEntities);

        // Fill totals
        List<Long> totals = new ArrayList<>();
        List<Measure<?>> allTotalMeasures = new ArrayList<>();

        fillTotals(secondDimensionTypeValues, secondDimensionTotalMeasures, measureType, totals, allTotalMeasures);

        // Fill all total
        long allTotal = getMeasureValue(allTotalMeasures, measureType);

        return resultTriFunction.apply(
                secondDimensionTypeValues.values(),
                measureValueEntities,
                totalsBiFunction.apply(totals, allTotal));
    }

    /**
     * Filters by third dimension values.
     *
     * @param measureType          measure type
     * @param filterDimensions     filter dimensions
     * @param measureMaps          measure maps
     * @param entry                entry of measure map
     * @param firstDimensionValue  first dimension value
     * @param secondEntryDimension second entry dimension
     * @param <T>                  first dimension type
     * @param <S>                  second dimension type
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T, S> void filterByThirdDimensionValues(MeasureType measureType,
                                                     Set<Dimension> filterDimensions,
                                                     MeasureMaps<T, S> measureMaps,
                                                     Map.Entry<Set<Dimension<?>>, Map<MeasureType, Measure<?>>> entry,
                                                     T firstDimensionValue,
                                                     Dimension<?> secondEntryDimension) {
        Set<Dimension<?>> entryDimensions = entry.getKey();

        for (Dimension<?> thirdEntryDimension : entryDimensions) {
            if (filterDimensions.contains(thirdEntryDimension)) {

                // filterByThirdDimensionValues
                Measure<?> measure = entry.getValue().get(measureType);

                if (measure != null) {
                    S secondDimensionValue = (S) secondEntryDimension.getValue();

                    // Measures of first and second dimension
                    measureMaps.measuresBySecondDimensionValue
                            .computeIfAbsent(secondDimensionValue, k -> new ArrayList<>())
                            .add(measure);

                    // Measures for total of first dimension
                    measureMaps.firstDimensionTotalMeasures
                            .computeIfAbsent(firstDimensionValue, k -> new ArrayList<>())
                            .add(measure);

                    // Measures for total of second dimension
                    measureMaps.secondDimensionTotalMeasures
                            .computeIfAbsent(secondDimensionValue, k -> new ArrayList<>())
                            .add(measure);
                }

                break;
            }
        }
    }

    /**
     * Fill resulting list.
     *
     * @param firstDimensionTypeValues      values of first dimension type
     * @param secondDimensionTypeValues     values of second dimension type
     * @param measureType                   measure type
     * @param entityTriFunction             result element function
     * @param measuresByFirstDimensionValue measures by first dimension value
     * @param firstDimensionTotalMeasures   total measures of first dimension
     * @param measureValueEntities          measure value entities
     * @param <T>                           first dimension type
     * @param <S>                           second dimension type
     * @param <V>                           result element type
     */
    private <T, S, V> void fillResultingList(DimensionTypeValues<T> firstDimensionTypeValues,
                                             DimensionTypeValues<S> secondDimensionTypeValues,
                                             MeasureType measureType,
                                             TriFunction<T, List<Long>, Long, V> entityTriFunction,
                                             Map<T, Map<S, List<Measure<?>>>> measuresByFirstDimensionValue,
                                             Map<T, List<Measure<?>>> firstDimensionTotalMeasures,
                                             List<V> measureValueEntities) {
        for (T firstDimensionValue : firstDimensionTypeValues.values()) {
            Map<S, List<Measure<?>>> measuresBySecondDimensionValue = measuresByFirstDimensionValue.get(firstDimensionValue);
            List<Long> measureValues;

            if (measuresBySecondDimensionValue == null) {
                measureValues = Collections.nCopies(secondDimensionTypeValues.values().size(), 0L);
            } else {
                measureValues = new ArrayList<>();

                for (S secondDimensionValue : secondDimensionTypeValues.values()) {
                    List<Measure<?>> measures = measuresBySecondDimensionValue.get(secondDimensionValue);
                    measureValues.add(getMeasureValue(measures, measureType));
                }
            }

            List<Measure<?>> measures = firstDimensionTotalMeasures.get(firstDimensionValue);
            long total = getMeasureValue(measures, measureType);

            measureValueEntities.add(entityTriFunction.apply(firstDimensionValue, measureValues, total));
        }
    }

    /**
     * Fill totals.
     *
     * @param secondDimensionTypeValues    values of second dimension type
     * @param secondDimensionTotalMeasures total measures of second dimension
     * @param measureType                  measure type
     * @param totals                       totals
     * @param allTotalMeasures             all total measures
     * @param <S>                          second dimension type
     */
    private <S> void fillTotals(DimensionTypeValues<S> secondDimensionTypeValues,
                                Map<S, List<Measure<?>>> secondDimensionTotalMeasures,
                                MeasureType measureType, List<Long> totals, List<Measure<?>> allTotalMeasures) {
        for (S secondDimensionValue : secondDimensionTypeValues.values()) {
            List<Measure<?>> measures = secondDimensionTotalMeasures.get(secondDimensionValue);
            long total = getMeasureValue(measures, measureType);

            totals.add(total);

            if (measures != null) {
                allTotalMeasures.addAll(measures);
            }
        }
    }
}
