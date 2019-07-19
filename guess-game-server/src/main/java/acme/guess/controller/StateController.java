package acme.guess.controller;

import acme.guess.dto.StartParametersDto;
import acme.guess.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * State controller.
 */
@Controller
@RequestMapping("/api/state")
public class StateController {
    private StateService stateService;

    @Autowired
    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @PostMapping("/parameters")
    @ResponseStatus(HttpStatus.OK)
    public void setStartParameters(@RequestBody StartParametersDto startParameters) {
        stateService.setStartParameters(StartParametersDto.convertFromDto(startParameters));
    }
}
