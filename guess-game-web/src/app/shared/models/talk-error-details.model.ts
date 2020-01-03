import { AnswerPair } from "./answer-pair.model";

export class TalkErrorDetails {
  constructor(
    public speakerFileName?: string,
    public speakerName?: string,
    public talkName?: string,
    public yourAnswers?: AnswerPair[]
  ) {
  }
}
