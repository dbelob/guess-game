import { Cube } from './cube.model';
import { Measure } from './measure.model';

export class OlapParameters {
  constructor(
    public cube?: Cube,
    public measure?: Measure,
    public organizerId?: number,
    public eventTypeId?: number,
    public speakerIds?: number[],
    public companyIds?: number[]
  ) {
  }
}
