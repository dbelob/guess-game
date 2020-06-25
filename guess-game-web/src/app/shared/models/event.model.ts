export class Event {
  constructor(
    public id?: number,
    public eventTypeId?: number,
    public name?: string,
    public startDate?: Date,
    public endDate?: Date,
    public siteLink?: string,
    public youtubeLink?: string,
    public placeCity?: string,
    public placeVenueAddress?: string,
    public mapCoordinates?: string,
    public eventTypeLogoFileName?: string,
    public duration?: number,
    public displayName?: string,
    public displayPlace?: string
  ) {
  }
}
