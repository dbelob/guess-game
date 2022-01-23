import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Speaker } from '../../../shared/models/speaker/speaker.model';
import { EntitiesListComponent } from '../entity-list.component';
import { SpeakerService } from '../../../shared/services/speaker.service';

@Component({
  selector: 'app-speakers-list',
  templateUrl: './speakers-list.component.html'
})
export class SpeakersListComponent extends EntitiesListComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public degreesImageDirectory = `${this.imageDirectory}/degrees`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;
  public twitterUrlPrefix = 'https://twitter.com';
  public gitHubUrlPrefix = 'https://github.com';
  public habrUrlPrefix = 'https://habr.com/users';

  public speakers: Speaker[] = [];

  constructor(public speakerService: SpeakerService, translateService: TranslateService) {
    super(translateService);
  }

  ngOnInit(): void {
    this.loadSpeakers(this.selectedLetter);
  }

  loadSpeakers(letter: string) {
    this.speakerService.getSpeakersByFirstLetter(letter)
      .subscribe(data => {
        this.speakers = data;
      });
  }

  isCurrentLetter(letter: string) {
    return (this.selectedLetter === letter);
  }

  changeLetter(letter: string) {
    this.selectedLetter = letter;

    this.paginatorFirst = 0;

    this.loadSpeakers(letter);
  }

  isSpeakersListVisible() {
    return (this.speakers.length > 0);
  }
}
