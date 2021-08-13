package guess.domain.statistics.olap.dimension;

import guess.domain.source.Company;
import guess.domain.statistics.olap.DimensionType;

import java.util.Set;

/**
 * Company dimension.
 */
public class CompanyDimension extends Dimension<Company> {
    public CompanyDimension(Set<Company> values) {
        super(DimensionType.COMPANY, values);
    }
}
