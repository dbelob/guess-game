package guess.domain.source.contentful.talk.fields;

import com.fasterxml.jackson.annotation.JsonSetter;
import guess.domain.source.contentful.ContentfulLink;

import java.util.List;

public class ContentfulTalkFieldsMobius extends ContentfulTalkFields {
    @Override
    @JsonSetter("talkPresentation")
    public void setPresentations(List<ContentfulLink> presentations) {
        super.setPresentations(presentations);
    }

    @Override
    @JsonSetter("presentationLink")
    public void setMaterial(String material) {
        super.setMaterial(material);
    }
}
