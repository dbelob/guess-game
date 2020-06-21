import { Speaker } from './speaker.model';

export class Talk {
  constructor(
    public id?: string,
    public name?: string,
    public talkDate?: Date,
    public language?: string,
    public eventId?: number,
    public eventName?: string,
    public eventTypeLogoFileName?: string,
    public speakers?: Speaker[],
    public speakersString?: string
  ) {
  }
}
