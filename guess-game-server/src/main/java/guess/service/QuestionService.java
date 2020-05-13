package guess.service;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessType;
import guess.domain.question.QuestionSet;
import guess.domain.source.EventType;

import java.time.LocalDate;
import java.util.List;

/**
 * Question service.
 */
public interface QuestionService {
    List<QuestionSet> getQuestionSets();

    Long getDefaultQuestionSetId(LocalDate date);

    List<EventType> getEventTypes();

    List<Integer> getQuantities(List<Long> questionSetIds, GuessType guessType) throws QuestionSetNotExistsException;
}
