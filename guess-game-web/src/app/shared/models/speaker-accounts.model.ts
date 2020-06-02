import { QuestionAnswers } from "./question-answers.model";
import { SpeakerPair } from "./speaker-pair.model";

export class SpeakerAccounts extends QuestionAnswers {
  constructor(
    public speaker?: SpeakerPair,
    public twitter0?: string,
    public gitHub0?: string,
    public twitter1?: string,
    public gitHub1?: string,
    public twitter2?: string,
    public gitHub2?: string,
    public twitter3?: string,
    public gitHub3?: string
  ) {
    super();
  }
}
