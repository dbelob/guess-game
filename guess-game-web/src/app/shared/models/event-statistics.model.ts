import { EventMetrics } from './event-metrics.model';

export class EventStatistics {
  constructor(
    public eventMetricsList?: EventMetrics[],
    public totals?: EventMetrics
  ) {
  }
}
