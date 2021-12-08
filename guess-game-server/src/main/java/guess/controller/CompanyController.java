package guess.controller;

import guess.domain.source.Company;
import guess.dto.common.SelectedEntitiesDto;
import guess.dto.company.CompanyDto;
import guess.service.CompanyService;
import guess.service.LocaleService;
import guess.util.LocalizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;

/**
 * Company controller.
 */
@RestController
@RequestMapping("/api/company")
public class CompanyController {
    private final CompanyService companyService;
    private final LocaleService localeService;

    @Autowired
    public CompanyController(CompanyService companyService, LocaleService localeService) {
        this.companyService = companyService;
        this.localeService = localeService;
    }

    @GetMapping("/first-letters-companies")
    public List<CompanyDto> getCompaniesByFirstLetters(@RequestParam String firstLetters, HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<Company> companies = companyService.getCompaniesByFirstLetters(firstLetters, language);

        return CompanyDto.convertToDto(companies, language).stream()
                .sorted(Comparator.comparing(CompanyDto::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @PostMapping("/selected-companies")
    public List<CompanyDto> getSelectedCompanies(@RequestBody SelectedEntitiesDto selectedEntities, HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<Company> companies = companyService.getCompaniesByIds(selectedEntities.getIds());

        return CompanyDto.convertToDto(companies, language).stream()
                .sorted(Comparator.comparing(CompanyDto::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @GetMapping("/first-letters-company-names")
    public List<String> getCompanyNamesByFirstLetters(@RequestParam String firstLetters, HttpSession httpSession) {
        var language = localeService.getLanguage(httpSession);
        List<Company> companies = companyService.getCompaniesByFirstLetters(firstLetters, language);

        return companies.stream()
                .map(c -> LocalizationUtils.getString(c.getName(), language))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }
}
