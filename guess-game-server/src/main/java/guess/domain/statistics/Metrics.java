package guess.domain.statistics;

import java.util.Objects;

/**
 * Metrics.
 */
public class Metrics {
    private final long talksQuantity;
    private final long javaChampionsQuantity;
    private final long mvpsQuantity;

    public Metrics(long talksQuantity, long javaChampionsQuantity, long mvpsQuantity) {
        this.talksQuantity = talksQuantity;
        this.javaChampionsQuantity = javaChampionsQuantity;
        this.mvpsQuantity = mvpsQuantity;
    }

    public long getTalksQuantity() {
        return talksQuantity;
    }

    public long getJavaChampionsQuantity() {
        return javaChampionsQuantity;
    }

    public long getMvpsQuantity() {
        return mvpsQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var metrics = (Metrics) o;
        return talksQuantity == metrics.talksQuantity &&
                javaChampionsQuantity == metrics.javaChampionsQuantity &&
                mvpsQuantity == metrics.mvpsQuantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(talksQuantity, javaChampionsQuantity, mvpsQuantity);
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "talksQuantity=" + talksQuantity +
                ", javaChampionsQuantity=" + javaChampionsQuantity +
                ", mvpsQuantity=" + mvpsQuantity +
                '}';
    }
}
