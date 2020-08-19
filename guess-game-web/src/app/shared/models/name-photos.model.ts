import { QuestionAnswers } from './question-answers.model';

export class NamePhotos extends QuestionAnswers {
  constructor(
    public name?: string,
    public filePhotoName0?: string,
    public filePhotoName1?: string,
    public filePhotoName2?: string,
    public filePhotoName3?: string
  ) {
    super();
  }
}
