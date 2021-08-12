package guess.domain.statistics.olap;

import java.util.List;

public enum CubeType {
    EVENT_TYPES(List.of(MeasureType.DURATION, MeasureType.EVENTS_QUANTITY, MeasureType.TALKS_QUANTITY, MeasureType.SPEAKERS_QUANTITY,
            MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY)),
    SPEAKERS(List.of(MeasureType.TALKS_QUANTITY, MeasureType.EVENTS_QUANTITY, MeasureType.EVENT_TYPES_QUANTITY,
            MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY)),
    COMPANIES(List.of(MeasureType.SPEAKERS_QUANTITY, MeasureType.TALKS_QUANTITY, MeasureType.EVENTS_QUANTITY, MeasureType.EVENT_TYPES_QUANTITY,
            MeasureType.JAVA_CHAMPIONS_QUANTITY, MeasureType.MVPS_QUANTITY));

    private final List<MeasureType> measureTypes;

    CubeType(List<MeasureType> measureTypes) {
        this.measureTypes = measureTypes;
    }

    public List<MeasureType> getMeasureTypes() {
        return measureTypes;
    }
}
