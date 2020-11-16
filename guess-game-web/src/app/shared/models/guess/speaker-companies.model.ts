import { QuestionAnswers } from './question-answers.model';
import { SpeakerPair } from '../result/speaker-pair.model';

export class SpeakerCompanies extends QuestionAnswers {
  constructor(
    public speaker?: SpeakerPair,
    public companyName0?: string,
    public companyName1?: string,
    public companyName2?: string,
    public companyName3?: string
  ) {
    super();
  }
}
