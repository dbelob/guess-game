package guess.domain.source.contentful;

import guess.domain.source.contentful.error.ContentfulError;

import java.util.List;

public class ContentfulResponse<T> {
    private Long total;
    private Long skip;
    private Long limit;
    private List<T> items;
    private List<ContentfulError> errors;
    private ContentfulIncludes includes;

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

    public List<ContentfulError> getErrors() {
        return errors;
    }

    public void setErrors(List<ContentfulError> errors) {
        this.errors = errors;
    }

    public ContentfulIncludes getIncludes() {
        return includes;
    }

    public void setIncludes(ContentfulIncludes includes) {
        this.includes = includes;
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
