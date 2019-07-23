import { QuestionAnswers } from "./question-answers.model";

export class NamePictures extends QuestionAnswers {
  constructor(
    public name?: string,
    public fileName0?: string,
    public fileName1?: string,
    public fileName2?: string,
    public fileName3?: string
  ) {
    super();
  }
}
