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
    return this.stateService.getState().pipe(
      map(state => {
          const actualUrl: string = stateSnapshot.url;
          let expectedUrl: string;

          switch (state) {
            case State.StartState: {
              expectedUrl = '/start';
              break;
            }
            case State.GuessNameState: {
              expectedUrl = '/guess/name';
              break;
            }
            case State.GuessPictureState: {
              expectedUrl = '/guess/picture';
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
