export class EventMetrics {
  constructor(
    public id?: number,
    public name?: string,
    public logoFileName?: string,
    public startDate?: Date,
    public duration?: number,
    public talksQuantity?: number,
    public speakersQuantity?: number,
    public javaChampionsQuantity?: number,
    public mvpsQuantity?: number
  ) {
  }
}
