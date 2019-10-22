package guess.domain.source.contentful;

import java.util.List;

public class ContentfulResponse<T> {
    private Long total;
    private Long skip;
    private Long limit;
    private List<T> items;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSkip() {
        return skip;
    }

    public void setSkip(Long skip) {
        this.skip = skip;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Response{" +
                "total=" + total +
                ", skip=" + skip +
                ", limit=" + limit +
                ", items=" + items +
                '}';
    }
}