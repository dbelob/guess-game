import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-speakers-search',
  templateUrl: './speakers-search.component.html'
})
export class SpeakersSearchComponent implements OnInit {
  public name: string;
  public company: string;
  public twitter: string;
  public gitHub: string;
  public isJavaChampion = false;
  public isMvp = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  onLanguageChange() {
    // TODO: implement
  }

  onJavaChampionChange(checked: boolean) {
    // TODO: implement
  }

  onMvpChange(checked: boolean) {
    // TODO: implement
  }

  search() {
    // TODO: implement
  }

  clear() {
    // TODO: implement
  }
}
