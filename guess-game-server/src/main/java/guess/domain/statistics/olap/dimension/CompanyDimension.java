package guess.domain.statistics.olap.dimension;

import guess.domain.source.Company;

/**
 * Company dimension.
 */
public class CompanyDimension extends Dimension<Company> {
    public CompanyDimension(Object value) {
        super(Company.class, value);
    }
}
