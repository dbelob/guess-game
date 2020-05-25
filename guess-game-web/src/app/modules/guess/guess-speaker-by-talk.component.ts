import { Component, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { TalkSpeakers } from "../../shared/models/talk-speakers.model";
import { StateService } from "../../shared/services/state.service";
import { AnswerService } from "../../shared/services/answer.service";
import { State } from "../../shared/models/state.model";

@Component({
  selector: 'app-guess-speaker',
  templateUrl: './guess-speaker-by-talk.component.html'
})
export class GuessSpeakerByTalkComponent implements OnInit {
  private imageDirectory: string = 'assets/images';
  private eventsImageDirectory: string = `${this.imageDirectory}/events`;
  private speakersImageDirectory: string = `${this.imageDirectory}/speakers`;
  public talkSpeakers: TalkSpeakers = new TalkSpeakers();
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
    this.stateService.getTalkSpeakers()
      .subscribe(data => {
          if (data) {
            this.talkSpeakers = data;
            this.title = `${this.talkSpeakers.questionSetName} (${this.talkSpeakers.currentIndex + 1}/${this.talkSpeakers.totalNumber})`;
            this.imageSource0 = `${this.speakersImageDirectory}/${this.talkSpeakers.speakerFileName0}`;
            this.imageSource1 = `${this.speakersImageDirectory}/${this.talkSpeakers.speakerFileName1}`;
            this.imageSource2 = `${this.speakersImageDirectory}/${this.talkSpeakers.speakerFileName2}`;
            this.imageSource3 = `${this.speakersImageDirectory}/${this.talkSpeakers.speakerFileName3}`;

            if (this.talkSpeakers.logoFileName) {
              this.logoImageSource = `${this.eventsImageDirectory}/${this.talkSpeakers.logoFileName}`;
            }
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.talkSpeakers.currentIndex, id)
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
