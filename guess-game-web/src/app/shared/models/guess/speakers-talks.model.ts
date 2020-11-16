import { QuestionAnswers } from './question-answers.model';
import { SpeakerPair } from '../result/speaker-pair.model';

export class SpeakersTalks extends QuestionAnswers {
  constructor(
    public speakers?: SpeakerPair[],
    public talkName0?: string,
    public talkName1?: string,
    public talkName2?: string,
    public talkName3?: string
  ) {
    super();
  }
}
