import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-speaker',
  templateUrl: './speaker.component.html'
})
export class SpeakerComponent implements OnInit {
  private id: number;

  constructor(activatedRoute: ActivatedRoute) {
    console.log('id: ' + this.id);

    activatedRoute.params.subscribe(params => {
      console.log('id (params): ' + params['id']);
    });
  }

  ngOnInit(): void {
  }
}
