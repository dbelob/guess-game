import { OlapEntityMetrics } from './olap-entity-metrics.model';
import { OlapEntityStatistics } from "./olap-entity-statistics.model";
import { OlapEventTypeMetrics } from "./olap-event-type-metrics.model";

export class OlapCompanyMetrics extends OlapEntityMetrics {
  constructor(
    public id?: number,
    public name?: string,
    public eventTypeStatistics?: OlapEntityStatistics<number, OlapEventTypeMetrics>
  ) {
    super();
  }
}
