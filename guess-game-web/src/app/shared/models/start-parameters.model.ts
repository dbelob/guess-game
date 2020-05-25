export class StartParameters {
  constructor(
    public questionSetIds?: number[],
    public quantity?: number,
    public guessMode?: string
  ) {
  }
}
