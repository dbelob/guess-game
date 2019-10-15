import { ErrorPair } from "./error-pair.model";

export class TalkErrorDetails {
  constructor(
    public speakerFileName?: string,
    public speakerName?: string,
    public talkName?: string,
    public wrongAnswers?: ErrorPair[]
  ) {
  }
}
