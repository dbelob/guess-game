package guess.dao;

import guess.domain.source.Place;

import java.util.List;

/**
 * Place DAO.
 */
public interface PlaceDao {
    List<Place> getPlaces();
}
