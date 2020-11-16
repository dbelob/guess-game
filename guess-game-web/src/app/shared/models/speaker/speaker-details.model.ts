import { Speaker } from './speaker.model';
import { Talk } from '../talk/talk.model';

export class SpeakerDetails {
  constructor(
    public speaker?: Speaker,
    public talks?: Talk[]
  ) {
  }
}
