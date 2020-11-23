import { EntitySpeakers } from './entity-speakers.model';

export class CompanySpeakers extends EntitySpeakers {
  constructor(
    public companyName?: string
  ) {
    super();
  }
}
