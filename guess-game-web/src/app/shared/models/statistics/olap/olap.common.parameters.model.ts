import { CubeType } from "./cube-type.model";
import { MeasureType } from "./measure-type.model";

export class OlapCommonParameters {
    constructor(
        public cubeType?: CubeType,
        public measureType?: MeasureType,
        public conferences?: boolean,
        public meetups?: boolean,
        public organizerId?: number,
        public eventTypeIds?: number[]
    ) {
    }
}
