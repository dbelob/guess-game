export class StartParameters {
  constructor(
    public eventTypeIds?: number[],
    public eventIds?: number[],
    public guessMode?: string,
    public quantity?: number
  ) {
  }
}
