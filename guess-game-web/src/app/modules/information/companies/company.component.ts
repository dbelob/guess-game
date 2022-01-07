import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { CompanyDetails } from '../../../shared/models/company/company-details.model';
import { CompanyService } from '../../../shared/services/company.service';
import { getSpeakersWithCompaniesString } from '../../general/utility-functions';

@Component({
  selector: 'app-company',
  templateUrl: './company.component.html'
})
export class CompanyComponent implements OnInit {
  private imageDirectory = 'assets/images';
  public degreesImageDirectory = `${this.imageDirectory}/degrees`;
  public speakersImageDirectory = `${this.imageDirectory}/speakers`;
  public twitterUrlPrefix = 'https://twitter.com';
  public gitHubUrlPrefix = 'https://github.com';
  public habrUrlPrefix = 'https://habr.com/users';

  private id: number;
  public companyDetails: CompanyDetails = new CompanyDetails();
  public multiSortMeta: any[] = [];

  constructor(public companyService: CompanyService, public translateService: TranslateService,
              private activatedRoute: ActivatedRoute) {
    this.multiSortMeta.push({field: 'displayName', order: 1});
    this.multiSortMeta.push({field: 'companiesString', order: 1});
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
    this.companyService.getCompany(id)
      .subscribe(data => {
        this.companyDetails = this.getCompanyDetailsWithFilledAttributes(data);
      });
  }

  getCompanyDetailsWithFilledAttributes(companyDetails: CompanyDetails): CompanyDetails {
    if (companyDetails?.speakers) {
      companyDetails.speakers = getSpeakersWithCompaniesString(companyDetails.speakers);
    }

    return companyDetails;
  }

  onLanguageChange() {
    this.loadCompany(this.id);
  }

  isCompanyLinksVisible() {
    return this.companyDetails.company?.siteLink;
  }

  isSpeakersListVisible() {
    return ((this.companyDetails.speakers) && (this.companyDetails.speakers.length > 0));
  }
}
