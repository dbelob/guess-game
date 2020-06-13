export class Event {
  constructor(
    public id?: number,
    public eventTypeId?: number,
    public name?: string,
    public startDate?: Date,
    public endDate?: Date,
    public siteLink?: string,
    public youtubeLink?: string,
    public displayName?: string
  ) {
  }
}
