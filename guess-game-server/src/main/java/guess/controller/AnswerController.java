package guess.controller;

import guess.domain.ErrorDetails;
import guess.domain.Result;
import guess.dto.ResultDto;
import guess.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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
    public void addAnswer(@RequestParam int questionIndex, @RequestParam long answerId, HttpSession httpSession) {
        answerService.setAnswer(questionIndex, answerId, httpSession);
    }

    @GetMapping("/result")
    @ResponseBody
    public ResultDto getResult(HttpSession httpSession) {
        Result result = answerService.getResult(httpSession);
        List<ErrorDetails> errorDetailsList = answerService.getErrorDetailsList(httpSession);

        return ResultDto.convertToDto(result, errorDetailsList);
    }
}
