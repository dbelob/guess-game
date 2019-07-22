export class Result {
  constructor(
    public correctAnswers?: number,
    public wrongAnswers?: number,
    public skippedAnswers?: number,
    public correctPercents?: number,
    public wrongPercents?: number,
    public skippedPercents?: number
  ) {
  }
}
