import { SpeakerPair } from './speaker-pair.model';

export class CompanyErrorDetails {
  constructor(
    public speakers?: SpeakerPair[],
    public companyNames?: string[],
    public yourAnswers?: SpeakerPair[]
  ) {
  }
}
