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
    private final long id;
    private final String name;

    private QuestionSetDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static List<QuestionSetDto> convertToDto(List<QuestionSet> questionSets, Language language) {
        return questionSets.stream()
                .map(a -> new QuestionSetDto(a.getId(), LocalizationUtils.getString(a.getName(), language)))
                .collect(Collectors.toList());
    }
}
