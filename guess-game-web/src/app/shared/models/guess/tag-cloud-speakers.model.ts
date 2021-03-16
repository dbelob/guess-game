import { EntitySpeakers } from './entity-speakers.model';

export class TagCloudSpeakers extends EntitySpeakers {
  constructor(
    public image?: string
  ) {
    super();
  }
}
