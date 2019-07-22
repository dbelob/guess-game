package acme.guess.controller;

import acme.guess.domain.Result;
import acme.guess.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Answer controller.
 */
@Controller
@RequestMapping("/api/answer")
public class AnswerController {
    private AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/answers")
    @ResponseStatus(HttpStatus.OK)
    public void addAnswer(@RequestParam int questionIndex, @RequestParam long answerId) {
        answerService.setAnswer(questionIndex, answerId);
    }

    @GetMapping("/result")
    @ResponseBody
    public Result getResult() {
        return answerService.getResult();
    }
}
