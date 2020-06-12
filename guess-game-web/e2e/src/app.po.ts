import { browser, by, element } from 'protractor';

export class AppPage {
  navigateTo() {
    return browser.get('/');
  }

  getImgAltValue() {
    return element(by.css('app-root img')).getAttribute('alt');
  }
}
