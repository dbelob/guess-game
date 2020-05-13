export class EventType {
  constructor(
    public id?: number,
    public conference?: boolean,
    public name?: string,
    public description?: string,
    public siteLink?: string,
    public vkLink?: string,
    public twitterLink?: string,
    public facebookLink?: string,
    public youtubeLink?: string,
    public telegramLink?: string,
    public logoFileName?: string
  ) {
  }
}
