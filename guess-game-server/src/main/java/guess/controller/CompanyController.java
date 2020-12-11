package guess.controller;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.service.CompanyService;
import guess.service.LocaleService;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Company controller.
 */
@Controller
@RequestMapping("/api/company")
public class CompanyController {
    private final CompanyService companyService;
    private final LocaleService localeService;

    @Autowired
    public CompanyController(CompanyService companyService, LocaleService localeService) {
        this.companyService = companyService;
        this.localeService = localeService;
    }

    @GetMapping("/first-letters-company-names")
    @ResponseBody
    public List<String> getCompanyNamesByFirstLetters(@RequestParam String firstLetters, HttpSession httpSession) {
        Language language = localeService.getLanguage(httpSession);
        List<Company> companies = companyService.getCompaniesByFirstLetters(firstLetters, language);

        return companies.stream()
                .map(c -> LocalizationUtils.getString(c.getName(), language))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }
}
