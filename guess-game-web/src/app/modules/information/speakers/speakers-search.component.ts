import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Speaker } from '../../../shared/models/speaker/speaker.model';
import { SpeakerService } from '../../../shared/services/speaker.service';
import { CompanyService } from '../../../shared/services/company.service';
import { getSpeakersWithCompaniesString, isStringEmpty } from '../../general/utility-functions';

@Component({
  selector: 'app-speakers-search',
  templateUrl: './speakers-search.component.html'
})
export class SpeakersSearchComponent {
  private imageDirectory = 'assets/images';
  public degreesImageDirectory = `${this.imageDirectory}/degrees`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;
  public twitterUrlPrefix = 'https://twitter.com';
  public gitHubUrlPrefix = 'https://github.com';
  public habrUrlPrefix = 'https://habr.com/users';

  public name: string;
  public company: string;
  public twitter: string;
  public gitHub: string;
  public isJavaChampion = false;
  public isMvp = false;

  public speakers: Speaker[] = [];

  private searched = false;
  public multiSortMeta: any[] = [];

  public companySuggestions: string[];

  constructor(private speakerService: SpeakerService, public translateService: TranslateService, private companyService: CompanyService) {
    this.multiSortMeta.push({field: 'displayName', order: 1});
    this.multiSortMeta.push({field: 'companiesString', order: 1});
  }

  loadSpeakers(name: string, company: string, twitter: string, gitHub: string, isJavaChampion: boolean, isMvp: boolean) {
    this.speakerService.getSpeakers(name, company, twitter, gitHub, isJavaChampion, isMvp)
      .subscribe(data => {
        this.speakers = getSpeakersWithCompaniesString(data);
        this.searched = true;
      });
  }

  onLanguageChange() {
    this.search();
  }

  onFilterChange(value: any) {
    this.searched = false;
  }

  search() {
    if (!this.isSearchDisabled()) {
      this.loadSpeakers(this.name, this.company, this.twitter, this.gitHub, this.isJavaChampion, this.isMvp);
    }
  }

  clear() {
    this.name = undefined;
    this.company = undefined;
    this.twitter = undefined;
    this.gitHub = undefined;
    this.isJavaChampion = false;
    this.isMvp = false;
    this.speakers = [];

    this.searched = false;
  }

  isSearchDisabled(): boolean {
    return (isStringEmpty(this.name) &&
      isStringEmpty(this.company) &&
      isStringEmpty(this.twitter) &&
      isStringEmpty(this.gitHub) &&
      (!this.isJavaChampion) &&
      (!this.isMvp));
  }

  isNoSpeakersFoundVisible() {
    return (this.searched && (this.speakers.length === 0));
  }

  isSpeakersListVisible() {
    return (this.searched && (this.speakers.length > 0));
  }

  companySearch(event) {
    this.companyService.getCompanyNamesByFirstLetters(event.query)
      .subscribe(data => {
          this.companySuggestions = data;
        }
      );
  }
}
