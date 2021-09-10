export abstract class OlapEntityMetrics {
  protected constructor(
    public measureValues?: number[],
    public total?: number
  ) {
  }
}
