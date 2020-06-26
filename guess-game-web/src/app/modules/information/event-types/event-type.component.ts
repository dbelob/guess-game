import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { EventTypeService } from '../../../shared/services/event-type.service';

@Component({
  selector: 'app-event-type',
  templateUrl: './event-type.component.html'
})
export class EventTypeComponent implements OnInit {
  // TODO: change to private
  public id: number;

  constructor(private eventTypeService: EventTypeService, public translateService: TranslateService,
              private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const idString: string = params['id'];
      const idNumber: number = Number(idString);

      if (!isNaN(idNumber)) {
        this.id = idNumber;
        this.loadEventType(this.id);
      }
    });
  }

  loadEventType(id: number) {
    // TODO: implement
  }

  onLanguageChange() {
    this.loadEventType(this.id);
  }
}
