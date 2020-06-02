import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import { catchError, map } from "rxjs/operators";
import { Observable, of } from "rxjs";
import { StateService } from "../services/state.service";
import { MessageService } from "../../modules/message/message.service";
import { State } from "../models/state.model";

@Injectable()
export class StateGuard implements CanActivate {
  constructor(public stateService: StateService, private messageService: MessageService, private router: Router) {
  }

  canActivate(routeSnapshot: ActivatedRouteSnapshot, stateSnapshot: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.stateService.getState()
      .pipe(
        map(state => {
            const actualUrl: string = stateSnapshot.url;
            let expectedUrl: string;

            switch (state) {
              case State.StartState: {
                expectedUrl = '/start';
                break;
              }
              case State.GuessNameByPhotoState: {
                expectedUrl = '/guess/name-by-photo';
                break;
              }
              case State.GuessPhotoByNameState: {
                expectedUrl = '/guess/photo-by-name';
                break;
              }
              case State.GuessTalkBySpeakerState: {
                expectedUrl = '/guess/talk-by-speaker';
                break;
              }
              case State.GuessSpeakerByTalkState: {
                expectedUrl = '/guess/speaker-by-talk';
                break;
              }
              case State.GuessAccountsBySpeakerState: {
                expectedUrl = '/guess/accounts-by-speaker';
                break;
              }
              case State.GuessSpeakerByAccountsState: {
                expectedUrl = '/guess/speaker-by-accounts';
                break;
              }
              case State.ResultState: {
                expectedUrl = '/result';
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
            return of(false)
          }
        )
      );
  }
}
