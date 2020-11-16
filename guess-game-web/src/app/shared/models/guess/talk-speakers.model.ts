import { EntitySpeakers } from './entity-speakers.model';

export class TalkSpeakers extends EntitySpeakers {
  constructor(
    public talkName?: string
  ) {
    super();
  }
}
