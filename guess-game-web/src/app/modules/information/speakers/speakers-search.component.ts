import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Speaker } from '../../../shared/models/speaker/speaker.model';
import { SpeakerService } from '../../../shared/services/speaker.service';
import { isStringEmpty } from '../../general/utility-functions';

@Component({
  selector: 'app-speakers-search',
  templateUrl: './speakers-search.component.html'
})
export class SpeakersSearchComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public degreesImageDirectory = `${this.imageDirectory}/degrees`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;
  public twitterUrlPrefix = 'https://twitter.com';
  public gitHubUrlPrefix = 'https://github.com';

  public name: string;
  public company: string;
  public twitter: string;
  public gitHub: string;
  public isJavaChampion = false;
  public isMvp = false;

  public speakers: Speaker[] = [];

  private searched = false;
  public multiSortMeta: any[] = [];

  constructor(public speakerService: SpeakerService, public translateService: TranslateService) {
    this.multiSortMeta.push({field: 'displayName', order: 1});
    this.multiSortMeta.push({field: 'company', order: 1});
  }

  ngOnInit(): void {
  }

  loadSpeakers(name: string, company: string, twitter: string, gitHub: string, isJavaChampion: boolean, isMvp: boolean) {
    this.speakerService.getSpeakers(name, company, twitter, gitHub, isJavaChampion, isMvp)
      .subscribe(data => {
        this.speakers = data;
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
}
