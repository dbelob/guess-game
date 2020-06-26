export class EventTypeMetrics {
  constructor(
    public id?: number,
    public displayName?: string,
    public sortName?: string,
    public conference?: boolean,
    public logoFileName?: string,
    public startDate?: Date,
    public age?: number,
    public duration?: number,
    public eventsQuantity?: number,
    public talksQuantity?: number,
    public speakersQuantity?: number,
    public javaChampionsQuantity?: number,
    public mvpsQuantity?: number
  ) {
  }
}
