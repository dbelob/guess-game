import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SpeakerCompanies } from '../../../shared/models/speaker-companies.model';
import { State } from '../../../shared/models/state.model';
import { StateService } from '../../../shared/services/state.service';
import { AnswerService } from '../../../shared/services/answer.service';

@Component({
  selector: 'app-guess-company-by-speaker',
  templateUrl: './guess-company-by-speaker.component.html'
})
export class GuessCompanyBySpeakerComponent implements OnInit {
  private imageDirectory = 'assets/images';
  private eventsImageDirectory = `${this.imageDirectory}/events`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;
  public speakerCompanies: SpeakerCompanies = new SpeakerCompanies();
  public title: string;
  public logoImageSource: string;

  constructor(private stateService: StateService, private answerService: AnswerService, private router: Router) {
  }

  ngOnInit(): void {
    this.loadQuestion();
  }

  loadQuestion() {
    this.stateService.getSpeakerCompanies()
      .subscribe(data => {
          if (data) {
            this.speakerCompanies = data;
            this.title = `${this.speakerCompanies.questionSetName} (${this.speakerCompanies.currentIndex + 1}/${this.speakerCompanies.totalNumber})`;

            if (this.speakerCompanies.logoFileName) {
              this.logoImageSource = `${this.eventsImageDirectory}/${this.speakerCompanies.logoFileName}`;
            }
          } else {
            this.result();
          }
        }
      );
  }

  answer(id: number) {
    this.answerService.setAnswer(this.speakerCompanies.currentIndex, id)
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
