import { SpeakerPair } from './speaker-pair.model';
import { TagCloudAnswer } from './tag-cloud-answer.model';

export class TagCloudErrorDetails {
  constructor(
    public speaker?: SpeakerPair,
    public image?: string,
    public yourAnswers?: TagCloudAnswer[]
  ) {
  }
}
