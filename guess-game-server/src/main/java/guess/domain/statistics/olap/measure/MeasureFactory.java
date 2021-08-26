package guess.domain.statistics.olap.measure;

import guess.domain.statistics.olap.MeasureType;

/**
 * Measure factory.
 */
public class MeasureFactory {
    private MeasureFactory() {
    }

    @SuppressWarnings("rawtypes")
    public static Measure create(MeasureType measureType, Object value) {
//        switch (measureType) {
//            case DURATION:
//                return new DurationMeasure(value);
//        }

        //TODO: implement
        return null;
    }
}
