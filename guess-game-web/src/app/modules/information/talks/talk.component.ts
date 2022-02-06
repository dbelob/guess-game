import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { YouTubePlayer } from '@angular/youtube-player/youtube-player';
import { TranslateService } from '@ngx-translate/core';
import { TalkDetails } from '../../../shared/models/talk/talk-details.model';
import { TalkService } from '../../../shared/services/talk.service';
import { getEventDisplayName, getSpeakersWithCompaniesString } from '../../general/utility-functions';
import getVideoId from 'get-video-id';

@Component({
  selector: 'app-talk',
  templateUrl: './talk.component.html'
})
export class TalkComponent implements AfterViewInit, OnInit, OnDestroy {
  private imageDirectory = 'assets/images';
  public degreesImageDirectory = `${this.imageDirectory}/degrees`;
  public eventsImageDirectory = `${this.imageDirectory}/events`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;
  public twitterUrlPrefix = 'https://twitter.com';
  public gitHubUrlPrefix = 'https://github.com';
  public habrUrlPrefix = 'https://habr.com/users';

  private id: number;
  public talkDetails: TalkDetails = new TalkDetails();

  @ViewChild('youtubePlayerDiv') youtubePlayerDiv: ElementRef<HTMLDivElement>;
  @ViewChild('youtubePlayer') youtubePlayer: YouTubePlayer;
  private originalVideoWidth: number;
  private originalVideoHeight: number;

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

        this.translateService.onLangChange
          .subscribe(() => this.loadTalk(this.id));
      }
    });
  }

  ngAfterViewInit(): void {
    this.onResize();
    window.addEventListener('resize', this.onResize);
  }

  ngOnDestroy(): void {
    window.removeEventListener('resize', this.onResize);
  }

  loadTalk(id: number) {
    if (this.translateService.currentLang) {
      this.talkService.getTalk(id)
        .subscribe(data => {
          this.talkDetails = this.getTalkDetailsWithFilledAttributes(data);

          this.addYouTubePlayer();
        });
    }
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

          if (videoId && (videoId['service'] === 'youtube')) {
            videoLinksVideoIds.push(videoId['id']);
          }
        }
      );

      talkDetails.talk.videoLinksVideoIds = videoLinksVideoIds;
    }

    // Company names of speakers
    if (talkDetails?.speakers) {
      talkDetails.speakers = getSpeakersWithCompaniesString(talkDetails.speakers);
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

  onResize = (): void => {
    if (this.youtubePlayer) {
      if (!this.originalVideoWidth) {
        this.originalVideoWidth = this.youtubePlayer.width;
      }

      if (!this.originalVideoHeight) {
        this.originalVideoHeight = this.youtubePlayer.height;
      }

      const videoWidth = Math.min(this.youtubePlayerDiv.nativeElement.clientWidth, this.originalVideoWidth);

      if (videoWidth != this.youtubePlayer.width) {
        const videoHeight = videoWidth * this.originalVideoHeight / this.originalVideoWidth;

        this.youtubePlayer.width = videoWidth;
        this.youtubePlayer.height = videoHeight;
      }
    }
  }

  isPresentationLinksListVisible() {
    return ((this.talkDetails.talk?.presentationLinks) && (this.talkDetails.talk.presentationLinks.length > 0));
  }

  isMaterialLinksListVisible() {
    return ((this.talkDetails.talk?.materialLinks) && (this.talkDetails.talk.materialLinks.length > 0));
  }

  isLinksListsVisible() {
    return this.isPresentationLinksListVisible() || this.isMaterialLinksListVisible();
  }

  isVideoLinksVideoIdsListVisible() {
    return ((this.talkDetails.talk?.videoLinksVideoIds) && (this.talkDetails.talk.videoLinksVideoIds.length > 0));
  }

  isSpeakersListVisible() {
    return ((this.talkDetails.speakers) && (this.talkDetails.speakers.length > 0));
  }
}
