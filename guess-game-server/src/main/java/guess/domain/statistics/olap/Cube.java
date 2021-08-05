package guess.domain.statistics.olap;

import java.util.List;

public enum Cube {
    EVENT_TYPES(List.of(Measure.DURATION, Measure.EVENTS_QUANTITY, Measure.TALKS_QUANTITY, Measure.SPEAKERS_QUANTITY,
            Measure.JAVA_CHAMPIONS_QUANTITY, Measure.MVPS_QUANTITY)),
    SPEAKERS(List.of(Measure.TALKS_QUANTITY, Measure.EVENTS_QUANTITY, Measure.EVENT_TYPES_QUANTITY,
            Measure.JAVA_CHAMPIONS_QUANTITY, Measure.MVPS_QUANTITY)),
    COMPANIES(List.of(Measure.SPEAKERS_QUANTITY, Measure.TALKS_QUANTITY, Measure.EVENTS_QUANTITY, Measure.EVENT_TYPES_QUANTITY,
            Measure.JAVA_CHAMPIONS_QUANTITY, Measure.MVPS_QUANTITY));

    private final List<Measure> measures;

    Cube(List<Measure> measures) {
        this.measures = measures;
    }

    public List<Measure> getMeasures() {
        return measures;
    }
}
