import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SpeakersTalks } from '../../../shared/models/guess/speakers-talks.model';
import { State } from '../../../shared/models/state.model';
import { StateService } from '../../../shared/services/state.service';
import { AnswerService } from '../../../shared/services/answer.service';

@Component({
  selector: 'app-guess-talk-by-speaker',
  templateUrl: './guess-talk-by-speaker.component.html'
})
export class GuessTalkBySpeakerComponent implements OnInit {
  private imageDirectory = 'assets/images';
  private eventsImageDirectory = `${this.imageDirectory}/events`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;
  public speakersTalks: SpeakersTalks = new SpeakersTalks();
  public title: string;
  public logoImageSource: string;

  constructor(private stateService: StateService, private answerService: AnswerService, private router: Router) {
  }

  ngOnInit(): void {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getSpeakerTalks()
      .subscribe(data => {
          if (data) {
            this.speakersTalks = data;
            this.title = `${this.speakersTalks.questionSetName} (${this.speakersTalks.currentIndex + 1}/${this.speakersTalks.totalNumber})`;

            if (this.speakersTalks.logoFileName) {
              this.logoImageSource = `${this.eventsImageDirectory}/${this.speakersTalks.logoFileName}`;
            }
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.speakersTalks.currentIndex, id)
      .subscribe(data => {
          this.loadQuestion();
        }
      );
  }

  result() {
    this.stateService.setState(State.ResultState)
      .subscribe(data => {
          this.router.navigateByUrl('/game/result');
        }
      );
  }

  cancel() {
    this.router.navigateByUrl('/game/cancel');
  }
}
