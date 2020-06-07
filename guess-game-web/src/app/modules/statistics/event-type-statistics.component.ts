import { Component, OnInit } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";
import { AnswerService } from "../../shared/services/answer.service";
import { Router } from "@angular/router";

@Component({
  selector: 'app-event-type-statistics',
  templateUrl: './event-type-statistics.component.html'
})
export class EventTypeStatisticsComponent implements OnInit {
  constructor(public translateService: TranslateService, private router: Router) {
  }

  ngOnInit(): void {
  }

  onLanguageChange() {
    //TODO: implement
  }

  game() {
    this.router.navigateByUrl('/start');
  }
}
