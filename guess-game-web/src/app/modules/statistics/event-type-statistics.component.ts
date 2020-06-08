import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-event-type-statistics',
  templateUrl: './event-type-statistics.component.html'
})
export class EventTypeStatisticsComponent implements OnInit {
  public conferences = true;
  public meetups = true;

  constructor(public translateService: TranslateService, private router: Router) {
  }

  ngOnInit(): void {
  }

  onLanguageChange() {
    // TODO: implement
  }

  onEventTypeKindChange(checked: boolean) {
    console.log('checked: ' + checked + ', conferences: ' + this.conferences + ', meetups: ' + this.meetups);
    // TODO: implement
  }

  game() {
    this.router.navigateByUrl('/start');
  }
}
