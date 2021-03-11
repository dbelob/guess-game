import { QuestionAnswers } from './question-answers.model';
import { SpeakerPair } from '../result/speaker-pair.model';

export class SpeakerTagClouds extends QuestionAnswers {
  constructor(
    public speaker?: SpeakerPair,
  ) {
    super();
  }
}
