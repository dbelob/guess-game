package acme.guess.controller;

import acme.guess.domain.QuestionSet;
import acme.guess.dto.QuestionSetDto;
import acme.guess.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public List<Integer> getQuantities() {
        return questionService.getQuantities();
    }
}
