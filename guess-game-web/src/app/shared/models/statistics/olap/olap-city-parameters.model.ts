import { CubeType } from './cube-type.model';
import { MeasureType } from './measure-type.model';

export class OlapCityParameters {
  constructor(
    public cubeType?: CubeType,
    public measureType?: MeasureType,
    public eventTypeId?: number
  ) {
  }
}
