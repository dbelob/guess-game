import { CubeType } from './cube-type.model';
import { Measure } from './measure.model';

export class OlapParameters {
  constructor(
    public cubeType?: CubeType,
    public measure?: Measure,
    public organizerId?: number,
    public eventTypeId?: number,
    public speakerIds?: number[],
    public companyIds?: number[]
  ) {
  }
}
