import { CubeType } from './cube-type.model';
import { MeasureType } from './measure-type.model';

export class OlapSpeakerParameters {
  constructor(
    public cubeType?: CubeType,
    public measureType?: MeasureType,
    public companyId?: number,
    public eventTypeId?: number
  ) {
  }
}
