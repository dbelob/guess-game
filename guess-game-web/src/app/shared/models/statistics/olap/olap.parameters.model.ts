import { OlapCommonParameters } from "./olap.common.parameters.model";
import { CubeType } from "./cube-type.model";
import { MeasureType } from "./measure-type.model";

export class OlapParameters extends OlapCommonParameters {
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
        super(cubeType, measureType, conferences, meetups, organizerId, eventTypeIds);
    }
}
