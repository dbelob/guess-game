import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Speaker } from '../../../shared/models/speaker.model';
import { SpeakerService } from '../../../shared/services/speaker.service';

@Component({
  selector: 'app-speakers-list',
  templateUrl: './speakers-list.component.html'
})
export class SpeakersListComponent implements OnInit {
  private readonly DEFAULT_LETTER = 'A';
  private readonly RUSSIAN_LANG = 'ru';

  private imageDirectory = 'assets/images';
  public degreesImageDirectory = `${this.imageDirectory}/degrees`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;

  public enLetters: string[] = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
    'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
  public ruLetters: string[] = ['А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р',
    'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Э', 'Ю', 'Я'];
  public selectedLetter = this.DEFAULT_LETTER;

  public speakers: Speaker[] = [];

  constructor(public speakerService: SpeakerService, public translateService: TranslateService) {
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

  onLanguageChange() {
    this.selectedLetter = this.DEFAULT_LETTER;
    this.loadSpeakers(this.DEFAULT_LETTER);
  }

  isCurrentLetter(letter: string) {
    return (this.selectedLetter === letter);
  }

  changeLetter(letter: string) {
    this.selectedLetter = letter;
    this.loadSpeakers(letter);
  }

  isRuLettersVisible(): boolean {
    return (this.translateService.currentLang === this.RUSSIAN_LANG);
  }

  isSpeakersListVisible() {
    return (this.speakers.length > 0);
  }
}
