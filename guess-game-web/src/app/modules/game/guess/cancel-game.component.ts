import { Component } from '@angular/core';
import { GameState } from '../../../shared/models/game-state.model';
import { StateService } from '../../../shared/services/state.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cancel-game',
  templateUrl: './cancel-game.component.html'
})
export class CancelGameComponent {
  constructor(private stateService: StateService, private router: Router) {
  }

  cancel() {
    this.stateService.setState(GameState.ResultState)
      .subscribe(data => {
          this.router.navigateByUrl('/game/result');
        }
      );
  }
}
