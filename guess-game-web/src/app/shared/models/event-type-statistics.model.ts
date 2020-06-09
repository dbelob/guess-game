import { EventTypeMetrics } from './event-type-metrics.model';

export class EventTypeStatistics {
  constructor(
    public eventTypeMetricsList?: EventTypeMetrics[],
    public totals?: EventTypeMetrics
  ) {
  }
}
