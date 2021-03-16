import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TagCloudSpeakers } from '../../../shared/models/guess/tag-cloud-speakers.model';
import { GameState } from '../../../shared/models/game-state.model';
import { StateService } from '../../../shared/services/state.service';
import { AnswerService } from '../../../shared/services/answer.service';

@Component({
  selector: 'app-guess-speaker-by-tag-cloud',
  templateUrl: './guess-speaker-by-tag-cloud.component.html'
})
export class GuessSpeakerByTagCloudComponent implements OnInit {
  private imageDirectory = 'assets/images';
  private eventsImageDirectory = `${this.imageDirectory}/events`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;
  private imageSourcePrefix = 'data:image/jpeg;base64,';
  public tagCloudSpeakers: TagCloudSpeakers = new TagCloudSpeakers();
  public title: string;
  public logoImageSource: string;
  public tagCloudImageSource: string;
  public speakerImageSource0: string;
  public speakerImageSource1: string;
  public speakerImageSource2: string;
  public speakerImageSource3: string;

  constructor(private stateService: StateService, private answerService: AnswerService, private router: Router) {
  }

  ngOnInit(): void {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getTagCloudSpeakers()
      .subscribe(data => {
          if (data) {
            this.tagCloudSpeakers = data;
            this.title =
              `${this.tagCloudSpeakers.questionSetName} (${this.tagCloudSpeakers.currentIndex + 1}/${this.tagCloudSpeakers.totalNumber})`;
            this.tagCloudImageSource = `${this.imageSourcePrefix}${this.tagCloudSpeakers.image}`;
            this.speakerImageSource0 = `${this.speakersImageDirectory}/${this.tagCloudSpeakers.speakerPhotoFileName0}`;
            this.speakerImageSource1 = `${this.speakersImageDirectory}/${this.tagCloudSpeakers.speakerPhotoFileName1}`;
            this.speakerImageSource2 = `${this.speakersImageDirectory}/${this.tagCloudSpeakers.speakerPhotoFileName2}`;
            this.speakerImageSource3 = `${this.speakersImageDirectory}/${this.tagCloudSpeakers.speakerPhotoFileName3}`;

            if (this.tagCloudSpeakers.logoFileName) {
              this.logoImageSource = `${this.eventsImageDirectory}/${this.tagCloudSpeakers.logoFileName}`;
            }
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.tagCloudSpeakers.currentIndex, id)
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
