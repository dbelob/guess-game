import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NamePhotos } from '../../../shared/models/guess/name-photos.model';
import { StateService } from '../../../shared/services/state.service';
import { AnswerService } from '../../../shared/services/answer.service';
import { GameState } from '../../../shared/models/game-state.model';

@Component({
  selector: 'app-guess-picture',
  templateUrl: './guess-photo-by-name.component.html'
})
export class GuessPhotoByNameComponent implements OnInit {
  private imageDirectory = 'assets/images';
  private eventsImageDirectory = `${this.imageDirectory}/events`;
  private speakersImageDirectory = `${this.imageDirectory}/speakers`;
  public namePhotos: NamePhotos = new NamePhotos();
  public title: string;
  public logoImageSource: string;
  public imageSource0: string;
  public imageSource1: string;
  public imageSource2: string;
  public imageSource3: string;

  constructor(private stateService: StateService, private answerService: AnswerService, private router: Router) {
  }

  ngOnInit(): void {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getNamePhotos()
      .subscribe(data => {
          if (data) {
            this.namePhotos = data;
            this.title = `${this.namePhotos.questionSetName} (${this.namePhotos.currentIndex + 1}/${this.namePhotos.totalNumber})`;
            this.imageSource0 = `${this.speakersImageDirectory}/${this.namePhotos.photoFileName0}`;
            this.imageSource1 = `${this.speakersImageDirectory}/${this.namePhotos.photoFileName1}`;
            this.imageSource2 = `${this.speakersImageDirectory}/${this.namePhotos.photoFileName2}`;
            this.imageSource3 = `${this.speakersImageDirectory}/${this.namePhotos.photoFileName3}`;

            if (this.namePhotos.logoFileName) {
              this.logoImageSource = `${this.eventsImageDirectory}/${this.namePhotos.logoFileName}`;
            }
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.namePhotos.currentIndex, id)
      .subscribe(data => {
          this.loadQuestion();
        }
      );
  }

  result() {
    this.stateService.setState(GameState.ResultState)
      .subscribe(data => {
          this.router.navigateByUrl('/game/result');
        }
      );
  }

  cancel() {
    this.router.navigateByUrl('/game/cancel');
  }
}
