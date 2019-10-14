import { QuestionAnswers } from "./question-answers.model";

export class SpeakerTalks extends QuestionAnswers {
  constructor(
    public speakerFileName?: string,
    public speakerName?: string,
    public talkName0?: string,
    public talkName1?: string,
    public talkName2?: string,
    public talkName3?: string
  ) {
    super();
  }
}
