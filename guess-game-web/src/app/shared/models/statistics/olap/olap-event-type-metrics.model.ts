import { OlapEntityMetrics } from './olap-entity-metrics.model';

export class OlapEventTypeMetrics extends OlapEntityMetrics {
  constructor(
    public id?: number,
    public displayName?: string,
    public conference?: boolean,
    public logoFileName?: string,
    public organizerName?: string
  ) {
    super();
  }
}
