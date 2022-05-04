package pl.kurs.vet.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.vet.model.dto.CheckDto;
import pl.kurs.vet.request.CreateCheckVisitCommand;
import pl.kurs.vet.request.CreateVisitCommand;
import pl.kurs.vet.response.ConfirmResponse;
import pl.kurs.vet.response.VisitSaveResponse;
import pl.kurs.vet.service.VisitService;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/visit/")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<VisitSaveResponse> save(@RequestBody @Valid CreateVisitCommand visitCommand) throws MessagingException {
        return new ResponseEntity<VisitSaveResponse>(visitService.save(visitCommand), HttpStatus.CREATED);
    }

    @GetMapping(value = "/confirm/{token}")
    public ResponseEntity<ConfirmResponse> confirm(@PathVariable(value = "token") String token) throws Exception {
        return new ResponseEntity<ConfirmResponse>(visitService.confirm(token), HttpStatus.OK);

    }

    @PostMapping(value = "/check/")
    public ResponseEntity<List<CheckDto>> check(@RequestBody @Valid CreateCheckVisitCommand visitCommand) {
        return new ResponseEntity<List<CheckDto>>(visitService.check(visitCommand), HttpStatus.OK);
    }

    @GetMapping(value = "/cancel/{token}")
    public ResponseEntity<ConfirmResponse> deleteVisit(@PathVariable(value = "token") String token) {
        return new ResponseEntity<ConfirmResponse>(visitService.deleteByToken(token), HttpStatus.OK);
    }

}
