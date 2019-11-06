package guess.controller;

import guess.dao.exception.QuestionSetNotExistsException;
import guess.domain.GuessType;
import guess.domain.Language;
import guess.domain.question.QuestionSet;
import guess.dto.start.QuestionSetDto;
import guess.service.LocaleService;
import guess.service.QuestionService;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Question controller.
 */
@Controller
@RequestMapping("/api/question")
public class QuestionController {
    private QuestionService questionService;
    private LocaleService localeService;

    @Autowired
    public QuestionController(QuestionService questionService, LocaleService localeService) {
        this.questionService = questionService;
        this.localeService = localeService;
    }

    @GetMapping("/sets")
    @ResponseBody
    public List<QuestionSetDto> getQuestionSets(HttpSession httpSession) {
        List<QuestionSet> questionSets = questionService.getQuestionSets();
        Language language = localeService.getLanguage(httpSession);

        questionSets.sort(Comparator.comparing(o -> LocalizationUtils.getName(o.getName(), language)));

        return QuestionSetDto.convertToDto(questionSets, language);
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
