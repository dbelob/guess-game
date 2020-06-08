import { SpeakerPair } from './speaker-pair.model';

export class AccountAnswer {
  constructor(
    public speaker?: SpeakerPair,
    public twitter?: string,
    public gitHub?: string
  ) {
  }
}
