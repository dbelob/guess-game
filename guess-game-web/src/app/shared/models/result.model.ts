import { GuessMode } from "./guess-type.model";
import { SpeakerErrorDetails } from "./speaker-error-details.model";
import { TalkErrorDetails } from "./talk-error-details.model";

export class Result {
  constructor(
    public correctAnswers?: number,
    public wrongAnswers?: number,
    public skippedAnswers?: number,
    public correctPercents?: number,
    public wrongPercents?: number,
    public skippedPercents?: number,
    public guessMode?: GuessMode,
    public speakerErrorDetailsList?: SpeakerErrorDetails[],
    public talkErrorDetailsList?: TalkErrorDetails[]
  ) {
  }
}
