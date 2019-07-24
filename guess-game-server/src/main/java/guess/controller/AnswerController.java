package guess.controller;

import guess.domain.ErrorDetails;
import guess.domain.Result;
import guess.dto.ResultDto;
import guess.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResultDto getResult() {
        Result result = answerService.getResult();
        List<ErrorDetails> errorDetailsList = answerService.getErrorDetailsList();

        return ResultDto.convertToDto(result, errorDetailsList);
    }
}
