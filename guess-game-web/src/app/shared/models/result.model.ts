import { GuessType } from "./guess-type.model";
import { ErrorDetails } from "./error-details.model";

export class Result {
  constructor(
    public correctAnswers?: number,
    public wrongAnswers?: number,
    public skippedAnswers?: number,
    public correctPercents?: number,
    public wrongPercents?: number,
    public skippedPercents?: number,
    public guessType?: GuessType,
    public errorDetailsList?: ErrorDetails[]
  ) {
  }
}
