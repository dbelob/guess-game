import { Component, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { SpeakersTalks } from "../../shared/models/speakers-talks.model";
import { StateService } from "../../shared/services/state.service";
import { AnswerService } from "../../shared/services/answer.service";
import { State } from "../../shared/models/state.model";

@Component({
  selector: 'app-guess-talk',
  templateUrl: './guess-talk-by-speaker.component.html'
})
export class GuessTalkBySpeakerComponent implements OnInit {
  private imageDirectory: string = 'assets/images';
  private eventsImageDirectory: string = `${this.imageDirectory}/events`;
  public speakersImageDirectory: string = `${this.imageDirectory}/speakers`;
  public speakerTalks: SpeakersTalks = new SpeakersTalks();
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
            this.speakerTalks = data;
            this.title = `${this.speakerTalks.questionSetName} (${this.speakerTalks.currentIndex + 1}/${this.speakerTalks.totalNumber})`;

            if (this.speakerTalks.logoFileName) {
              this.logoImageSource = `${this.eventsImageDirectory}/${this.speakerTalks.logoFileName}`;
            }
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.speakerTalks.currentIndex, id)
      .subscribe(data => {
          this.loadQuestion();
        }
      );
  }

  result() {
    this.stateService.setState(State.ResultState)
      .subscribe(data => {
          this.router.navigateByUrl('/result');
        }
      );
  }

  cancel() {
    this.router.navigateByUrl('/cancel');
  }
}
