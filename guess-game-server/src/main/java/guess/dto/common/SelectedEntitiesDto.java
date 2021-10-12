package guess.dto.common;

import java.util.List;

/**
 * Selected entities DTO.
 */
public class SelectedEntitiesDto {
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
