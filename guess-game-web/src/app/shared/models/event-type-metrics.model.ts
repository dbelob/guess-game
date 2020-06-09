export class EventTypeMetrics {
  constructor(
    public displayName?: string,
    public sortName?: string,
    public conference?: boolean,
    public startDate?: Date,
    public age?: number,
    public duration?: number,
    public eventsQuantity?: number,
    public talksQuantity?: number,
    public speakersQuantity?: number
  ) {
  }
}
