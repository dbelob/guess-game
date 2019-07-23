import { QuestionAnswers } from "./question-answers.model";

export class PictureNames extends QuestionAnswers {
  constructor(
    public fileName?: string,
    public name0?: string,
    public name1?: string,
    public name2?: string,
    public name3?: string
  ) {
    super();
  }
}
