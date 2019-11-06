package guess.dto.start;

import guess.domain.Language;
import guess.domain.question.QuestionSet;
import guess.util.LocalizationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Question set DTO.
 */
public class QuestionSetDto {
    private long id;
    private String name;

    public QuestionSetDto() {
    }

    private QuestionSetDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<QuestionSetDto> convertToDto(List<QuestionSet> questionSets, Language language) {
        return questionSets.stream()
                .map(a -> new QuestionSetDto(a.getId(), LocalizationUtils.getName(a.getName(), language)))
                .collect(Collectors.toList());
    }
}
