import { Event } from '../event/event.model';
import { Speaker } from '../speaker/speaker.model';

export class Talk {
  constructor(
    public id?: number,
    public name?: string,
    public talkDate?: Date,
    public event?: Event,
    public eventTypeLogoFileName?: string,
    public speakers?: Speaker[],
    public speakersString?: string,
    public description?: string,
    public talkDay?: number,
    public talkTime?: Date,
    public track?: number,
    public language?: string,
    public presentationLinks?: string[],
    public videoLinks?: string[],
    public videoLinksVideoIds?: string[]
  ) {
  }
}
