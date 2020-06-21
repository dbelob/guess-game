import { Speaker } from './speaker.model';
import { Talk } from './talk.model';

export class TalkDetails {
  constructor(
    public talk?: Talk,
    public speakers?: Speaker[]
  ) {
  }
}
