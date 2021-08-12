package guess.domain.statistics.olap.dimension;

import guess.domain.source.Company;
import guess.domain.statistics.olap.Dimension;
import guess.domain.statistics.olap.DimensionType;

/**
 * Company dimension.
 */
public class CompanyDimension extends Dimension<Company> {
    public CompanyDimension(Company value) {
        super(DimensionType.COMPANY, value);
    }
}
