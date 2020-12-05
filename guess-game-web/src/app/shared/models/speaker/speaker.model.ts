import { Company } from '../company/company.model';

export class Speaker {
  constructor(
    public id?: string,
    public photoFileName?: string,
    public displayName?: string,
    public name?: string,
    public company?: string,      // TODO: delete after load change
    public companies?: Company[],
    public bio?: string,
    public twitter?: string,
    public gitHub?: string,
    public javaChampion?: boolean,
    public mvp?: boolean,
    public mvpReconnect?: boolean,
    public anyMvp?: boolean
  ) {
  }
}
