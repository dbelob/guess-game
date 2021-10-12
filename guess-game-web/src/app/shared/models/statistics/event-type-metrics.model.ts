import { SortableEventTypeMetrics } from './sortable-event-type-metrics.model';

export class EventTypeMetrics implements SortableEventTypeMetrics {
  constructor(
    public id?: number,
    public displayName?: string,
    public conference?: boolean,
    public logoFileName?: string,
    public startDate?: Date,
    public age?: number,
    public duration?: number,
    public eventsQuantity?: number,
    public talksQuantity?: number,
    public speakersQuantity?: number,
    public javaChampionsQuantity?: number,
    public mvpsQuantity?: number,
    public organizerName?: string,
    public sortName?: string
  ) {
  }
}
