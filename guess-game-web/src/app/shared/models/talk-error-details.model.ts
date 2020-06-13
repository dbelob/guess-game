import { SpeakerPair } from './speaker-pair.model';

export class TalkErrorDetails {
  constructor(
    public speakers?: SpeakerPair[],
    public talkName?: string,
    public yourAnswers?: SpeakerPair[]
  ) {
  }
}
