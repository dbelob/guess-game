import { OlapEntityMetrics } from './olap-entity-metrics.model';
import { SortableEventTypeMetrics } from '../sortable-event-type-metrics.model';
import { OlapEntityStatistics } from './olap-entity-statistics.model';
import { OlapSpeakerMetrics } from './olap-speaker-metrics.model';

export class OlapEventTypeMetrics extends OlapEntityMetrics implements SortableEventTypeMetrics {
  constructor(
    public conference?: boolean,
    public logoFileName?: string,
    public organizerName?: string,
    public sortName?: string,
    public speakerStatistics?: OlapEntityStatistics<number, OlapSpeakerMetrics>,
    public companyId?: number
  ) {
    super();
  }
}
