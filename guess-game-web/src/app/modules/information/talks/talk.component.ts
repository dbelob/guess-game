import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { TalkDetails } from '../../../shared/models/talk/talk-details.model';
import { TalkService } from '../../../shared/services/talk.service';
import { getEventDisplayName } from '../../general/utility-functions';
import getVideoId from 'get-video-id';

@Component({
  selector: 'app-talk',
  templateUrl: './talk.component.html'
})
export class TalkComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public degreesImageDirectory = `${this.imageDirectory}/degrees`;
  public eventsImageDirectory = `${this.imageDirectory}/events`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;
  public twitterUrlPrefix = 'https://twitter.com';
  public gitHubUrlPrefix = 'https://github.com';

  private id: number;
  public talkDetails: TalkDetails = new TalkDetails();

  constructor(private talkService: TalkService, public translateService: TranslateService,
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
        this.talkDetails = this.getTalkDetailsWithFilledAttributes(data);

        this.addYouTubePlayer();
      });
  }

  getTalkDetailsWithFilledAttributes(talkDetails: TalkDetails): TalkDetails {
    // Event display name
    if (talkDetails?.talk?.event) {
      talkDetails.talk.event.displayName = getEventDisplayName(talkDetails.talk.event, this.translateService);
    }

    // YouTube video ids
    if (talkDetails?.talk?.videoLinks) {
      // const getVideoId = require('get-video-id');
      const videoLinksVideoIds: string[] = [];

      talkDetails.talk.videoLinks.forEach(v => {
          const videoId = getVideoId(v);

          if (videoId && (videoId.service === 'youtube')) {
            videoLinksVideoIds.push(videoId.id);
          }
        }
      );

      talkDetails.talk.videoLinksVideoIds = videoLinksVideoIds;
    }

    return talkDetails;
  }

  addYouTubePlayer() {
    // This code loads the IFrame Player API code asynchronously, according to the instructions at
    // https://developers.google.com/youtube/iframe_api_reference#Getting_Started
    const tag = document.createElement('script');

    tag.src = 'https://www.youtube.com/iframe_api';
    document.body.appendChild(tag);
  }

  onLanguageChange() {
    this.loadTalk(this.id);
  }

  isPresentationLinksListVisible() {
    return ((this.talkDetails.talk?.presentationLinks) && (this.talkDetails.talk.presentationLinks.length > 0));
  }

  isVideoLinksVideoIdsListVisible() {
    return ((this.talkDetails.talk?.videoLinksVideoIds) && (this.talkDetails.talk.videoLinksVideoIds.length > 0));
  }

  isSpeakersListVisible() {
    return ((this.talkDetails.speakers) && (this.talkDetails.speakers.length > 0));
  }
}
