package guess.dto.company;

import guess.domain.Language;
import guess.domain.source.Company;
import guess.domain.source.Speaker;
import guess.dto.speaker.SpeakerBriefDto;

import java.util.List;

/**
 * Company details DTO.
 */
public record CompanyDetailsDto(CompanyDto company, List<SpeakerBriefDto> speakers) {
    public static CompanyDetailsDto convertToDto(Company company, List<Speaker> speakers, Language language) {
        return new CompanyDetailsDto(
                CompanyDto.convertToDto(company, language),
                SpeakerBriefDto.convertToBriefDto(speakers, language)
        );
    }
}
