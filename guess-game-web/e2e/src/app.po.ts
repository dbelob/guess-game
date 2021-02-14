import { browser, by, element } from 'protractor';

export class AppPage {
  navigateTo() {
    return browser.get('/');
  }

  getElementAttributeValue(elementName: string, attributeName: string) {
    return element(by.css(elementName)).getAttribute(attributeName);
  }
}
