import { SpeakerPair } from "./speaker-pair.model";
import { AccountAnswer } from "./account-answer.model";

export class AccountErrorDetails {
  constructor(
    public speaker?: SpeakerPair,
    public twitter?: string,
    public gitHub?: string,
    public yourAnswers?: AccountAnswer[]
  ) {
  }
}
