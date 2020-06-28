import { Event } from './event.model';
import { Speaker } from './speaker.model';
import { Talk } from './talk.model';

export class EventDetails {
  constructor(
    public event?: Event,
    public speakers?: Speaker[],
    public talks?: Talk[]
  ) {
  }
}
