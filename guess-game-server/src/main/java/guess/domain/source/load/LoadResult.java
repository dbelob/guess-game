package guess.domain.source.load;

/**
 * Load result.
 *
 * @param <T> type
 */
public record LoadResult<T>(T itemToDelete, T itemToAppend, T itemToUpdate) {
}
