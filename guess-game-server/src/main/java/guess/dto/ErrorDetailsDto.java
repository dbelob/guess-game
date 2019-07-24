package guess.dto;

import guess.domain.ErrorDetails;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Error details DTO.
 */
public class ErrorDetailsDto {
    private String fileName;
    private String name;
    private long wrongAnswers;

    public ErrorDetailsDto(String fileName, String name, long wrongAnswers) {
        this.fileName = fileName;
        this.name = name;
        this.wrongAnswers = wrongAnswers;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public long getWrongAnswers() {
        return wrongAnswers;
    }

    private static ErrorDetailsDto convertToDto(ErrorDetails errorDetails, String directoryName) {
        return new ErrorDetailsDto(
                String.format("%s/%s", directoryName, errorDetails.getQuestion().getFileName()),
                errorDetails.getQuestion().getName(),
                errorDetails.getWrongAnswers());
    }

    public static List<ErrorDetailsDto> convertToDto(List<ErrorDetails> errorDetailsList, String directoryName) {
        return errorDetailsList.stream()
                .map(e -> convertToDto(e, directoryName))
                .collect(Collectors.toList());
    }
}
