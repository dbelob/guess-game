import { Event } from './event.model';
import { Speaker } from './speaker.model';

export class Talk {
  constructor(
    public id?: string,
    public name?: string,
    public talkDate?: Date,
    public language?: string,
    public eventId?: number,
    public event?: Event,
    public eventTypeLogoFileName?: string,
    public speakers?: Speaker[],
    public speakersString?: string,
    public displayName?: string
  ) {
  }
}
