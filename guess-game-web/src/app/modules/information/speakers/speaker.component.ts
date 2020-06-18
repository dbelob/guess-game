import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SpeakerDetails } from '../../../shared/models/speaker-details.model';
import { SpeakerService } from '../../../shared/services/speaker.service';

@Component({
  selector: 'app-speaker',
  templateUrl: './speaker.component.html'
})
export class SpeakerComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  private id: number;
  public speakerDetails: SpeakerDetails = new SpeakerDetails();

  public multiSortMeta: any[] = [];

  constructor(public speakerService: SpeakerService, public translateService: TranslateService,
              private activatedRoute: ActivatedRoute) {
    this.multiSortMeta.push({field: 'talkDate', order: -1});
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const idString: string = params['id'];
      const idNumber: number = Number(idString);

      if (!isNaN(idNumber)) {
        this.id = idNumber;
        this.loadSpeakers(this.id);
      }
    });
  }

  loadSpeakers(id: number) {
    this.speakerService.getSpeaker(id)
      .subscribe(data => {
        this.speakerDetails = data;
      });
  }

  onLanguageChange() {
    this.loadSpeakers(this.id);
  }

  isTalksListVisible() {
    return ((this.speakerDetails.talks) && (this.speakerDetails.talks.length > 0));
  }
}
