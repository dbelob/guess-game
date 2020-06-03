import { QuestionAnswers } from "./question-answers.model";

export class AccountSpeakers extends QuestionAnswers {
  constructor(
    public twitter?: string,
    public gitHub?: string,
    public speakerFileName0?: string,
    public speakerName0?: string,
    public speakerFileName1?: string,
    public speakerName1?: string,
    public speakerFileName2?: string,
    public speakerName2?: string,
    public speakerFileName3?: string,
    public speakerName3?: string
  ) {
    super();
  }
}
