export class SpeakerMetrics {
  constructor(
    public name?: string,
    public fileName?: string,
    public javaChampion?: boolean,
    public mvp?: boolean,
    public mvpReconnect?: boolean,
    public anyMvp?: boolean,
    public talksQuantity?: number,
    public eventsQuantity?: number,
    public eventTypesQuantity?: number,
    public javaChampionsQuantity?: number,
    public mvpsQuantity?: number
  ) {
  }
}
