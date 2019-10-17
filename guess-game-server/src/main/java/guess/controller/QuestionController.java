package guess.controller;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessType;
import guess.domain.question.QuestionSet;
import guess.dto.start.QuestionSetDto;
import guess.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

/**
 * Question controller.
 */
@Controller
@RequestMapping("/api/question")
public class QuestionController {
    private QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/sets")
    @ResponseBody
    public List<QuestionSetDto> getQuestionSets() {
        List<QuestionSet> questionSets = questionService.getQuestionSets();

        return QuestionSetDto.convertToDto(questionSets);
    }

    @GetMapping("/default-set-id")
    @ResponseBody
    public Long getDefaultQuestionSetId() {
        return questionService.getDefaultQuestionSetId(LocalDate.now());
    }

    @GetMapping("/quantities")
    @ResponseBody
    public List<Integer> getQuantities(@RequestParam List<Long> questionSetIds, @RequestParam String guessType) throws QuestionSetNotExistsException {
        return questionService.getQuantities(questionSetIds, GuessType.valueOf(guessType));
    }
}
