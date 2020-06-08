import { Component, OnInit } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";
import { Router } from "@angular/router";
import { EventType } from "../../shared/models/event-type.model";

@Component({
  selector: 'app-event-type-statistics',
  templateUrl: './event-type-statistics.component.html'
})
export class EventTypeStatisticsComponent implements OnInit {
  public conference: boolean = true;
  public meetup: boolean = true;

  constructor(public translateService: TranslateService, private router: Router) {
  }

  ngOnInit(): void {
  }

  onLanguageChange() {
    //TODO: implement
  }

  onEventTypeKindChange(checked: boolean) {
    console.log('checked: ' + checked + ', conference: ' + this.conference + ', meetup: ' + this.meetup);
    //TODO: implement
  }

  game() {
    this.router.navigateByUrl('/start');
  }
}
