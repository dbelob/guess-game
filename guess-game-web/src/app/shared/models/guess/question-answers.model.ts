export abstract class QuestionAnswers {
  protected constructor(
    public questionSetName?: string,
    public currentIndex?: number,
    public totalNumber?: number,
    public logoFileName?: string,
    public id0?: number,
    public id1?: number,
    public id2?: number,
    public id3?: number,
    public invalid0?: boolean,
    public invalid1?: boolean,
    public invalid2?: boolean,
    public invalid3?: boolean,
    public valid0?: boolean,
    public valid1?: boolean,
    public valid2?: boolean,
    public valid3?: boolean
  ) {
  }
}
