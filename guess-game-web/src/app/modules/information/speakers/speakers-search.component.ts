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

  public name: string;
  public company: string;
  public twitter: string;
  public gitHub: string;
  public isJavaChampion = false;
  public isMvp = false;

  public speakers: Speaker[] = [];

  constructor(public speakerService: SpeakerService, public translateService: TranslateService) {
  }

  ngOnInit(): void {
  }

  loadSpeakers(name: string, company: string, twitter: string, gitHub: string, isJavaChampion: boolean, isMvp: boolean) {
    this.speakerService.getSpeakers(name, company, twitter, gitHub, isJavaChampion, isMvp)
      .subscribe(data => {
        this.speakers = data;
      });
  }

  onLanguageChange() {
    this.loadSpeakers(this.name, this.company, this.twitter, this.gitHub, this.isJavaChampion, this.isMvp);
  }

  onJavaChampionChange(checked: boolean) {
    // TODO: implement
  }

  onMvpChange(checked: boolean) {
    // TODO: implement
  }

  search() {
    this.loadSpeakers(this.name, this.company, this.twitter, this.gitHub, this.isJavaChampion, this.isMvp);
  }

  clear() {
    // TODO: implement
  }

  isSpeakersListVisible() {
    return (this.speakers.length > 0);
  }
}
