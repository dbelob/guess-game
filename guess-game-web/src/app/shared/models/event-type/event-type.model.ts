export class EventType {
  constructor(
    public id?: number,
    public conference?: boolean,
    public name?: string,
    public shortDescription?: string,
    public description?: string,
    public siteLink?: string,
    public vkLink?: string,
    public twitterLink?: string,
    public facebookLink?: string,
    public youtubeLink?: string,
    public telegramLink?: string,
    public logoFileName?: string,
    public displayName?: string,
    public inactive?: boolean,
    public organizerName?: string,
    public orderName?: string
  ) {
  }
}
