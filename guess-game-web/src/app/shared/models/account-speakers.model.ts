import { EntitySpeakers } from './entity-speakers.model';

export class AccountSpeakers extends EntitySpeakers {
  constructor(
    public twitter?: string,
    public gitHub?: string
  ) {
    super();
  }
}
