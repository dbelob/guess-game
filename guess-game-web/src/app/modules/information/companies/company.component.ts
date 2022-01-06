import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute } from "@angular/router";

@Component({
  selector: 'app-company',
  templateUrl: './company.component.html'
})
export class CompanyComponent implements OnInit {
  private id: number;

  constructor(public translateService: TranslateService, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const idString: string = params['id'];
      const idNumber: number = Number(idString);

      if (!isNaN(idNumber)) {
        this.id = idNumber;
        this.loadCompany(this.id);
      }
    });
  }

  loadCompany(id: number) {
    // TODO: implement
    console.log('Company id: ' + id);
  }

  onLanguageChange() {
    // TODO: implement
  }
}
