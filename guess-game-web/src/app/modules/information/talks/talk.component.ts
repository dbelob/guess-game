import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { TalkDetails } from '../../../shared/models/talk-details.model';
import { TalkService } from '../../../shared/services/talk.service';
import { getEventDisplayName, getTalksWithSpeakersString } from '../../general/utility-functions';

@Component({
  selector: 'app-talk',
  templateUrl: './talk.component.html'
})
export class TalkComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public eventsImageDirectory = `${this.imageDirectory}/events`;

  private id: number;
  public talkDetails: TalkDetails = new TalkDetails();

  constructor(public talkService: TalkService, public translateService: TranslateService,
              private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const idString: string = params['id'];
      const idNumber: number = Number(idString);

      if (!isNaN(idNumber)) {
        this.id = idNumber;
        this.loadTalk(this.id);
      }
    });
  }

  loadTalk(id: number) {
    this.talkService.getTalk(id)
      .subscribe(data => {
        this.talkDetails = this.getTalkDetailsWithTalkWithDisplayName(data);
      });
  }

  getTalkDetailsWithTalkWithDisplayName(talkDetails: TalkDetails): TalkDetails {
    if (talkDetails?.talk?.event) {
      talkDetails.talk.event.displayName = getEventDisplayName(talkDetails.talk.event, this.translateService);
    }

    return talkDetails;
  }

  onLanguageChange() {
    this.loadTalk(this.id);
  }

  isSpeakersListVisible() {
    // TODO: implement
  }
}
