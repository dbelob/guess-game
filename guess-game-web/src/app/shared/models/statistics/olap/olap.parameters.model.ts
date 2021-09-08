import { CubeType } from './cube-type.model';
import { MeasureType } from './measure-type.model';

export class OlapParameters {
  constructor(
    public cubeType?: CubeType,
    public measureType?: MeasureType,
    public conferences?: boolean,
    public meetups?: boolean,
    public organizerId?: number,
    public eventTypeIds?: number[],
    public speakerIds?: number[],
    public companyIds?: number[]
  ) {
  }
}
