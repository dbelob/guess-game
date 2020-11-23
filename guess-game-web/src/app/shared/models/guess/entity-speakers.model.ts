import { QuestionAnswers } from './question-answers.model';

export class EntitySpeakers extends QuestionAnswers {
  constructor(
    public speakerPhotoFileName0?: string,
    public speakerName0?: string,
    public speakerPhotoFileName1?: string,
    public speakerName1?: string,
    public speakerPhotoFileName2?: string,
    public speakerName2?: string,
    public speakerPhotoFileName3?: string,
    public speakerName3?: string
  ) {
    super();
  }
}
