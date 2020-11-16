import { Event } from './event.model';
import { Speaker } from '../speaker/speaker.model';
import { Talk } from '../talk/talk.model';

export class EventDetails {
  constructor(
    public event?: Event,
    public speakers?: Speaker[],
    public talks?: Talk[]
  ) {
  }
}
