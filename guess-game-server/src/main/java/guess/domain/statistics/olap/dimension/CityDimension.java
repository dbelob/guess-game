package guess.domain.statistics.olap.dimension;

/**
 * City dimension.
 */
public class CityDimension extends Dimension<City> {
    public CityDimension(Object value) {
        super(City.class, value);
    }
}
