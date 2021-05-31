package guess.domain.source.contentful.talk.fields;

import com.fasterxml.jackson.annotation.JsonSetter;
import guess.domain.source.contentful.ContentfulLink;

public class ContentfulTalkFieldsHolyJs extends ContentfulTalkFields {
    @Override
    @JsonSetter("presentation")
    public void setPresentation(ContentfulLink presentation) {
        super.setPresentation(presentation);
    }

    @Override
    @JsonSetter("presentationLink")
    public void setMaterial(String material) {
        super.setMaterial(material);
    }
}
