import { QuestionAnswers } from './question-answers.model';
import { SpeakerPair } from '../result/speaker-pair.model';

export class SpeakerTagClouds extends QuestionAnswers {
  constructor(
    public speaker?: SpeakerPair,
    public image0?: string,
    public image1?: string,
    public image2?: string,
    public image3?: string
  ) {
    super();
  }
}
