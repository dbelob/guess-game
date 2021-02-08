import { AppPage } from './app.po';

const mockServer = require('mockttp').getLocal();

describe('App', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  beforeEach(() => mockServer.start(8080));
  afterEach(() => mockServer.stop());

  it('should display welcome message', () => {
    mockServer.get('/api/event/default-event-home-info').thenReply(200, null);
    mockServer.get('/api/state/state').thenReply(200, '"START_STATE"');
    mockServer.get('/api/question/sets').thenReply(200, '[{"id":0,"name":"Question Set 1"},{"id":1,"name":"Question Set 2"},{"id":2,"name":"Question Set 3"}]');
    mockServer.get('/api/question/quantities').withQuery({'questionSetId': '0'}).thenReply(200, '[5,10]');

    page.navigateTo();
    expect(page.getImgAltValue()).toEqual('logo');
  });
});
