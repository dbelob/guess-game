package guess.domain.statistics;

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
}
