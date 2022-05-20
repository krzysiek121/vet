package pl.kurs.vet.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.kurs.vet.model.dto.CheckDto;
import pl.kurs.vet.request.CreateCheckVisitCommand;
import pl.kurs.vet.request.CreateVisitCommand;
import pl.kurs.vet.response.ConfirmResponse;
import pl.kurs.vet.response.VisitSaveResponse;
import pl.kurs.vet.service.VisitService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/visit/")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitSaveResponse save(@RequestBody @Valid CreateVisitCommand visitCommand, HttpServletRequest request) throws MessagingException {
        return visitService.save(visitCommand, request);
    }

    @GetMapping(value = "/confirm/{token}")
    @ResponseStatus(HttpStatus.OK)
    public ConfirmResponse confirm(@PathVariable(value = "token") String token) throws Exception {
        return visitService.confirm(token);

    }

    @PostMapping(value = "/check/")
    @ResponseStatus(HttpStatus.OK)
    public List<CheckDto> check(@RequestBody @Valid CreateCheckVisitCommand visitCommand) {
        return visitService.check(visitCommand);
    }

    @GetMapping(value = "/cancel/{token}")
    @ResponseStatus(HttpStatus.OK)
    public ConfirmResponse deleteVisit(@PathVariable(value = "token") String token) {
        return visitService.deleteByToken(token);
    }


}
