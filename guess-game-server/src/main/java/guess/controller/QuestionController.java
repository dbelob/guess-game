package guess.controller;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessType;
import guess.domain.QuestionSet;
import guess.dto.QuestionSetDto;
import guess.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/quantities")
    @ResponseBody
    public List<Integer> getQuantities(@RequestParam List<Long> questionSetIds) throws QuestionSetNotExistsException {
        //TODO: change to parameter
        GuessType guessType = GuessType.GUESS_NAME_TYPE;

        return questionService.getQuantities(questionSetIds, guessType);
    }
}
