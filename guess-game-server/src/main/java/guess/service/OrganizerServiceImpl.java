package guess.service;

import guess.dao.OrganizerDao;
import guess.domain.source.Organizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Organizer service implementation.
 */
@Service
public class OrganizerServiceImpl implements OrganizerService {
    private final OrganizerDao organizerDao;

    @Autowired
    public OrganizerServiceImpl(OrganizerDao organizerDao) {
        this.organizerDao = organizerDao;
    }

    @Override
    public List<Organizer> getOrganizers() {
        return organizerDao.getOrganizers();
    }
}
