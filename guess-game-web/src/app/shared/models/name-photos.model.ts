import { QuestionAnswers } from './question-answers.model';

export class NamePhotos extends QuestionAnswers {
  constructor(
    public name?: string,
    public photoFileName0?: string,
    public photoFileName1?: string,
    public photoFileName2?: string,
    public photoFileName3?: string
  ) {
    super();
  }
}
