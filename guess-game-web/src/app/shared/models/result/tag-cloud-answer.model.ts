import { SpeakerPair } from './speaker-pair.model';

export class TagCloudAnswer {
  constructor(
    public speaker?: SpeakerPair,
    public image?: string
  ) {
  }
}
