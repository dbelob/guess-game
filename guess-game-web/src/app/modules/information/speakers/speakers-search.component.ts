import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Speaker } from '../../../shared/models/speaker.model';
import { SpeakerService } from '../../../shared/services/speaker.service';

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

  constructor(public speakerService: SpeakerService, public translateService: TranslateService) {
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
    return this.isStringEmpty(this.name) &&
      this.isStringEmpty(this.company) &&
      this.isStringEmpty(this.twitter) &&
      this.isStringEmpty(this.gitHub) &&
      (!this.isJavaChampion) &&
      (!this.isMvp);
  }

  isStringEmpty(value: string): boolean {
    return (!value || (value.trim().length <= 0));
  }

  isNoSpeakersFoundVisible() {
    return (this.searched && (this.speakers.length === 0));
  }

  isSpeakersListVisible() {
    return (this.searched && (this.speakers.length > 0));
  }
}
