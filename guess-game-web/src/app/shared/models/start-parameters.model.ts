import { QuestionSet } from "./question-set.model";

export class StartParameters {
  constructor(
    public questionSetIds?: number[],
    public quantity?: number,
    public guessType?: string
  ) {
  }
}

