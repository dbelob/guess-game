package guess.domain.source.contentful.talk.fields;

import com.fasterxml.jackson.annotation.JsonSetter;
import guess.domain.source.contentful.ContentfulLink;

import java.util.List;

public class ContentfulTalkFieldsDotNext extends ContentfulTalkFields {
    @Override
    @JsonSetter("talksPresentation")
    public void setPresentations(List<ContentfulLink> presentations) {
        super.setPresentations(presentations);
    }

    @Override
    @JsonSetter("presentation")
    public void setPresentation(ContentfulLink presentation) {
        super.setPresentation(presentation);
    }
}
