import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CompanySpeakers } from '../../../shared/models/company-speakers.model';
import { State } from '../../../shared/models/state.model';
import { StateService } from '../../../shared/services/state.service';
import { AnswerService } from '../../../shared/services/answer.service';

@Component({
  selector: 'app-guess-speaker-by-company',
  templateUrl: './guess-speaker-by-company.component.html'
})
export class GuessSpeakerByCompanyComponent implements OnInit {
  private imageDirectory = 'assets/images';
  private eventsImageDirectory = `${this.imageDirectory}/events`;
  private speakersImageDirectory = `${this.imageDirectory}/speakers`;
  public companySpeakers: CompanySpeakers = new CompanySpeakers();
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
    this.stateService.getCompanySpeakers()
      .subscribe(data => {
          if (data) {
            this.companySpeakers = data;
            this.title = `${this.companySpeakers.questionSetName} (${this.companySpeakers.currentIndex + 1}/${this.companySpeakers.totalNumber})`;
            this.imageSource0 = `${this.speakersImageDirectory}/${this.companySpeakers.speakerPhotoFileName0}`;
            this.imageSource1 = `${this.speakersImageDirectory}/${this.companySpeakers.speakerPhotoFileName1}`;
            this.imageSource2 = `${this.speakersImageDirectory}/${this.companySpeakers.speakerPhotoFileName2}`;
            this.imageSource3 = `${this.speakersImageDirectory}/${this.companySpeakers.speakerPhotoFileName3}`;

            if (this.companySpeakers.logoFileName) {
              this.logoImageSource = `${this.eventsImageDirectory}/${this.companySpeakers.logoFileName}`;
            }
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.companySpeakers.currentIndex, id)
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
