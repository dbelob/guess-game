package guess.dto;

import guess.domain.ErrorDetails;
import guess.domain.GuessType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Error details DTO.
 */
public class ErrorDetailsDto {
    private String fileName;
    private String name;
    private List<String> wrongAnswers;

    public ErrorDetailsDto(String fileName, String name, List<String> wrongAnswers) {
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

    public List<String> getWrongAnswers() {
        return wrongAnswers;
    }

    private static ErrorDetailsDto convertToDto(ErrorDetails errorDetails, String directoryName, GuessType guessType) {
        List<String> wrongAnswers = errorDetails.getWrongAnswers().stream()
                .map(q -> (GuessType.GUESS_PICTURE_TYPE.equals(guessType)) ?
                        q.getName() :
                        String.format("%s/%s", directoryName, q.getFileName()))
                .collect(Collectors.toList());

        return new ErrorDetailsDto(
                String.format("%s/%s", directoryName, errorDetails.getQuestion().getFileName()),
                errorDetails.getQuestion().getName(),
                wrongAnswers);
    }

    public static List<ErrorDetailsDto> convertToDto(List<ErrorDetails> errorDetailsList, String directoryName, GuessType guessType) {
        return errorDetailsList.stream()
                .map(e -> convertToDto(e, directoryName, guessType))
                .collect(Collectors.toList());
    }
}
