package guess.domain.statistics;

/**
 * Abstract metrics.
 */
public class AbstractMetrics {
    private final long talksQuantity;
    private final long javaChampionsQuantity;
    private final long mvpsQuantity;

    public AbstractMetrics(long talksQuantity, long javaChampionsQuantity, long mvpsQuantity) {
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
