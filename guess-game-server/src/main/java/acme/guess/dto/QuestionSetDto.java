package acme.guess.dto;

import acme.guess.domain.QuestionSet;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Question set DTO.
 */
public class QuestionSetDto {
    private long id;
    private String name;

    public QuestionSetDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static List<QuestionSetDto> convertToDto(List<QuestionSet> questionSets) {
        return questionSets.stream()
                .map(a -> new QuestionSetDto(a.getId(), a.getName()))
                .collect(Collectors.toList());
    }
}
