import { OlapEntityStatistics } from './olap-entity-statistics.model';
import { OlapEventTypeMetrics } from './olap-event-type-metrics.model';
import { OlapSpeakerMetrics } from './olap-speaker-metrics.model';
import { OlapCompanyMetrics } from './olap-company-metrics.model';

export class OlapStatistics {
  constructor(
    public eventTypeStatistics?: OlapEntityStatistics<number, OlapEventTypeMetrics>,
    public speakerStatistics?: OlapEntityStatistics<number, OlapSpeakerMetrics>,
    public companyStatistics?: OlapEntityStatistics<number, OlapCompanyMetrics>
  ) {
  }
}
