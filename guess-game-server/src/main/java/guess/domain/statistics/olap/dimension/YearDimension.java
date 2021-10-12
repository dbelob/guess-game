package guess.domain.statistics.olap.dimension;

/**
 * Year dimension.
 */
public class YearDimension extends Dimension<Integer> {
    public YearDimension(Object value) {
        super(Integer.class, value);
    }
}
