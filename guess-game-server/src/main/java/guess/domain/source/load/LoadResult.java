package guess.domain.source.load;

/**
 * Load result.
 *
 * @param <T> type
 */
public class LoadResult<T> {
    private final T itemToDelete;
    private final T itemToAppend;
    private final T itemToUpdate;

    public LoadResult(T itemToDelete, T itemToAppend, T itemToUpdate) {
        this.itemToDelete = itemToDelete;
        this.itemToAppend = itemToAppend;
        this.itemToUpdate = itemToUpdate;
    }

    public T getItemToDelete() {
        return itemToDelete;
    }

    public T getItemToAppend() {
        return itemToAppend;
    }

    public T getItemToUpdate() {
        return itemToUpdate;
    }
}
