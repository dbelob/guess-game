import { Company } from './company.model';
import { Speaker } from '../speaker/speaker.model';

export class CompanyDetails {
  constructor(
    public company?: Company,
    public speakers?: Speaker[]
  ) {
  }
}
