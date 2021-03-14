import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SpeakerTagClouds } from '../../../shared/models/guess/speaker-tag-clouds.model';
import { GameState } from '../../../shared/models/game-state.model';
import { StateService } from '../../../shared/services/state.service';
import { AnswerService } from '../../../shared/services/answer.service';

@Component({
  selector: 'app-guess-tag-cloud-by-speaker',
  templateUrl: './guess-tag-cloud-by-speaker.component.html'
})
export class GuessTagCloudBySpeakerComponent implements OnInit {
  private imageDirectory = 'assets/images';
  private eventsImageDirectory = `${this.imageDirectory}/events`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;
  private imageSourcePrefix = 'data:image/jpeg;base64,';
  public speakerTagClouds: SpeakerTagClouds = new SpeakerTagClouds();
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
    this.stateService.getSpeakerTagClouds()
      .subscribe(data => {
          if (data) {
            this.speakerTagClouds = data;
            this.title =
              `${this.speakerTagClouds.questionSetName} (${this.speakerTagClouds.currentIndex + 1}/${this.speakerTagClouds.totalNumber})`;
            this.imageSource0 = `${this.imageSourcePrefix}${this.speakerTagClouds.image0}`;
            this.imageSource1 = `${this.imageSourcePrefix}${this.speakerTagClouds.image1}`;
            this.imageSource2 = `${this.imageSourcePrefix}${this.speakerTagClouds.image2}`;
            this.imageSource3 = `${this.imageSourcePrefix}${this.speakerTagClouds.image3}`;

            if (this.speakerTagClouds.logoFileName) {
              this.logoImageSource = `${this.eventsImageDirectory}/${this.speakerTagClouds.logoFileName}`;
            }
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.speakerTagClouds.currentIndex, id)
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
