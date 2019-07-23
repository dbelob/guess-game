export abstract class QuestionAnswers {
  constructor(
    public questionSetName?: string,
    public currentIndex?: number,
    public totalNumber?: number,
    public id0?: number,
    public id1?: number,
    public id2?: number,
    public id3?: number,
    public invalid0?: boolean,
    public invalid1?: boolean,
    public invalid2?: boolean,
    public invalid3?: boolean
  ) {
  }
}
