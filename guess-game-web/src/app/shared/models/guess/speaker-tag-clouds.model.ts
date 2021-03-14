import { QuestionAnswers } from './question-answers.model';
import { SpeakerPair } from '../result/speaker-pair.model';

export class SpeakerTagClouds extends QuestionAnswers {
  constructor(
    public speaker?: SpeakerPair,
    public image0?: any,
    public image1?: any,
    public image2?: any,
    public image3?: any
  ) {
    super();
  }
}
