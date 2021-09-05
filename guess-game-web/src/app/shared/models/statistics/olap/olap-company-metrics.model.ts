import { OlapEntityMetrics } from './olap-entity-metrics.model';

export class OlapCompanyMetrics extends OlapEntityMetrics {
  constructor(
    public id?: number,
    public name?: string
  ) {
    super();
  }
}
