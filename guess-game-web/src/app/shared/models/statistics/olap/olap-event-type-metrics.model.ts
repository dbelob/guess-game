import { OlapEntityMetrics } from './olap-entity-metrics.model';
import { SortableEventTypeMetrics } from "../sortable-event-type-metrics.model";

export class OlapEventTypeMetrics extends OlapEntityMetrics implements SortableEventTypeMetrics {
  constructor(
    public id?: number,
    public displayName?: string,
    public conference?: boolean,
    public logoFileName?: string,
    public organizerName?: string,
    public sortName?: string
  ) {
    super();
  }
}
