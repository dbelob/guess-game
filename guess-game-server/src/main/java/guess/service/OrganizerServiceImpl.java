package guess.service;

import guess.domain.Language;
import guess.domain.source.LocaleItem;
import guess.domain.source.Organizer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Organizer service implementation.
 */
@Service
public class OrganizerServiceImpl implements OrganizerService {
    @Override
    public List<Organizer> getOrganizers() {
        //TODO: implement
        return List.of(
                new Organizer(0, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Organizer0"))),
                new Organizer(1, List.of(new LocaleItem(Language.ENGLISH.getCode(), "Organizer1")))
        );
    }
}
