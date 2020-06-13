import { SpeakerMetrics } from './speaker-metrics.model';

export class SpeakerStatistics {
  constructor(
    public speakerMetricsList?: SpeakerMetrics[],
    public totals?: SpeakerMetrics
  ) {
  }
}
