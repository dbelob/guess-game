package guess.domain.source.contentful.talk.response;

import guess.domain.source.contentful.ContentfulResponse;
import guess.domain.source.contentful.talk.ContentfulTalk;
import guess.domain.source.contentful.talk.ContentfulTalkIncludes;
import guess.domain.source.contentful.talk.fields.ContentfulTalkFields;

public abstract class ContentfulTalkResponse<T extends ContentfulTalkFields> extends ContentfulResponse<ContentfulTalk<T>, ContentfulTalkIncludes> {
}
