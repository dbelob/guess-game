import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { catchError, map } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { StateService } from '../services/state.service';
import { MessageService } from '../../modules/message/message.service';
import { GameState } from '../models/game-state.model';

@Injectable()
export class GameStateGuard implements CanActivate {
  constructor(public stateService: StateService, private messageService: MessageService, private router: Router) {
  }

  canActivate(routeSnapshot: ActivatedRouteSnapshot, stateSnapshot: RouterStateSnapshot):
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.stateService.getState()
      .pipe(
        map(state => {
            const actualUrl: string = stateSnapshot.url;
            let expectedUrl: string;

            switch (state) {
              case GameState.StartState: {
                expectedUrl = '/game/start';
                break;
              }
              case GameState.GuessNameByPhotoState: {
                expectedUrl = '/game/guess/name-by-photo';
                break;
              }
              case GameState.GuessPhotoByNameState: {
                expectedUrl = '/game/guess/photo-by-name';
                break;
              }
              case GameState.GuessTalkBySpeakerState: {
                expectedUrl = '/game/guess/talk-by-speaker';
                break;
              }
              case GameState.GuessSpeakerByTalkState: {
                expectedUrl = '/game/guess/speaker-by-talk';
                break;
              }
              case GameState.GuessCompanyBySpeakerState:
                expectedUrl = '/game/guess/company-by-speaker';
                break;
              case GameState.GuessSpeakerByCompanyState:
                expectedUrl = '/game/guess/speaker-by-company';
                break;
              case GameState.GuessAccountBySpeakerState: {
                expectedUrl = '/game/guess/account-by-speaker';
                break;
              }
              case GameState.GuessSpeakerByAccountState: {
                expectedUrl = '/game/guess/speaker-by-account';
                break;
              }
              case GameState.GuessTagCloudBySpeakerState: {
                expectedUrl = '/game/guess/tag-cloud-by-speaker';
                break;
              }
              case GameState.GuessSpeakerByTagCloudState: {
                expectedUrl = '/game/guess/speaker-by-tag-cloud';
                break;
              }
              case GameState.ResultState: {
                expectedUrl = '/game/result';
                break;
              }
            }

            if (actualUrl === expectedUrl) {
              return true;
            } else {
              this.router.navigateByUrl(expectedUrl);
            }
          }
        ),
        catchError(response => {
            this.messageService.reportMessage(response);
            return of(false);
          }
        )
      );
  }
}
