package guess.domain.source.load;

import java.util.List;

/**
 * Load result.
 *
 * @param <T> type
 */
public class LoadResult<T> {
    private final List<T> itemsToDelete;
    private final List<T> itemsToAppend;
    private final List<T> itemsToUpdate;

    public LoadResult(List<T> itemsToDelete, List<T> itemsToAppend, List<T> itemsToUpdate) {
        this.itemsToDelete = itemsToDelete;
        this.itemsToAppend = itemsToAppend;
        this.itemsToUpdate = itemsToUpdate;
    }

    public List<T> getItemsToDelete() {
        return itemsToDelete;
    }

    public List<T> getItemsToAppend() {
        return itemsToAppend;
    }

    public List<T> getItemsToUpdate() {
        return itemsToUpdate;
    }
}
