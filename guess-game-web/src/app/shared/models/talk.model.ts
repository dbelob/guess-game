import { Event } from './event.model';
import { Speaker } from './speaker.model';

export class Talk {
  constructor(
    public id?: string,
    public name?: string,
    public talkDate?: Date,
    public event?: Event,
    public eventTypeLogoFileName?: string,
    public speakers?: Speaker[],
    public speakersString?: string,
    public description?: string,
    public talkDay?: number,
    public trackTime?: string,
    public track?: number,
    public language?: string,
    public presentationLinks?: string[],
    public videoLinks?: string[],
    public trackTimeDate?: Date,
  ) {
  }
}
