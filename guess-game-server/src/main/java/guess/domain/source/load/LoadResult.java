package guess.domain.source.load;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadResult<?> that = (LoadResult<?>) o;
        return Objects.equals(itemToDelete, that.itemToDelete) &&
                Objects.equals(itemToAppend, that.itemToAppend) &&
                Objects.equals(itemToUpdate, that.itemToUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemToDelete, itemToAppend, itemToUpdate);
    }

    @Override
    public String toString() {
        return "LoadResult{" +
                "itemToDelete=" + itemToDelete +
                ", itemToAppend=" + itemToAppend +
                ", itemToUpdate=" + itemToUpdate +
                '}';
    }
}
