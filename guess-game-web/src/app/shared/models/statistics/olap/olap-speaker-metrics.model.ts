import { OlapEntityMetrics } from './olap-entity-metrics.model';

export class OlapSpeakerMetrics extends OlapEntityMetrics {
  constructor(
    public id?: number,
    public name?: string,
    public photoFileName?: string
  ) {
    super();
  }
}
