import { CompanyMetrics } from './company-metrics.model';

export class CompanyStatistics {
  constructor(
    public companyMetricsList?: CompanyMetrics[],
    public totals?: CompanyMetrics
  ) {
  }
}
