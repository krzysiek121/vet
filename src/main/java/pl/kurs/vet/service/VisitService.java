package pl.kurs.vet.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.kurs.vet.exception.NoEmptySlotsException;
import pl.kurs.vet.exception.TimeVisitException;
import pl.kurs.vet.exception.TokenNotFoundException;
import pl.kurs.vet.exception.TooLateConfirmException;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.Patient;
import pl.kurs.vet.model.Visit;
import pl.kurs.vet.model.dto.CheckDto;
import pl.kurs.vet.model.dto.DoctorDtoCheck;
import pl.kurs.vet.repository.DoctorRepository;
import pl.kurs.vet.repository.PatientRepository;
import pl.kurs.vet.repository.VisitRepository;
import pl.kurs.vet.request.CreateCheckVisitCommand;
import pl.kurs.vet.request.CreateVisitCommand;
import pl.kurs.vet.response.ConfirmResponse;
import pl.kurs.vet.response.VisitSaveResponse;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitService {

    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ModelMapper modelMapper;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private final EmailSenderService emailSenderService;


    @Transactional
    public VisitSaveResponse save(CreateVisitCommand command, HttpServletRequest request) throws MessagingException {
        Doctor doctor = doctorService.findDoctorById(command.getDoctorId());
        Patient patient = patientService.findPatientById(command.getPatientId());

        LocalDateTime timeOfVisit = command.getDate();
        if (!checkOverlapping(timeOfVisit, doctor.getVisits())) {
            throw new TimeVisitException("DOCTOR_HAS_VISIT_THAT_DATE");
        }
        if (!checkOverlapping(timeOfVisit, patient.getVisits())) {
            throw new TimeVisitException("patient already has visit that date.");
        }

        Visit toSave = new Visit(doctor, patient, command.getDate(), generateNewToken(), LocalDateTime.now());


        String confirmationLink = getBaseUrl(request) + "/confirm/" + toSave.getToken();

        visitRepository.saveAndFlush(toSave);
        emailSenderService.sendConfirmationToEmail(patient.getEmail(), command.getDate().toString()
                , toSave.getPatient().getOwnerName(), confirmationLink);


        return new VisitSaveResponse(toSave.getId());
    }

    @Transactional
    public ConfirmResponse confirm(String token) throws Exception {

        Visit visit = visitRepository.findByToken(token).orElseThrow(() -> new TokenNotFoundException("TOKEN_NOT_FOUND"));

        if (LocalDateTime.now().isBefore(visit.getTimeSendConfirmation().plusMinutes(5))) {

            visit.setConfirmed(true);

            return new ConfirmResponse("VISIT_CONFIRMED");
        } else {
            throw new TooLateConfirmException("TOO_LATE_TO_CONFIRM");
        }


    }

    public String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public static boolean checkOverlapping(LocalDateTime start, Set<Visit> list) {

        if (list != null || !list.isEmpty()) {
            LocalDateTime slot = start;
            for (LocalDateTime t : list.stream().map(s -> s.getData()).collect(Collectors.toList())) {
                if (isOverlapping(slot, slot.plusMinutes(60), t, t.plusMinutes(60))) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }


    public static boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime
            start2, LocalDateTime end2) {
        return !isAfterOrEqual(start1, end2) && !isAfterOrEqual(start2, end1);
    }


    @Transactional
    public List<CheckDto> check(CreateCheckVisitCommand visitCommand) {

        LocalDateTime start = visitCommand.getFrom();
        LocalDateTime stop = visitCommand.getTo();


        List<Doctor> list = new ArrayList<>();
        for (Doctor d : doctorRepository.findByTypeAndAndAnimalType(visitCommand.getType(), visitCommand.getAnimal())) {
            list.add(d);
        }


        return getSlots(list, start, stop, modelMapper);

    }

    public List<CheckDto> getSlots(List<Doctor> doctors, LocalDateTime start, LocalDateTime stop, ModelMapper modelMapper) {
        List<CheckDto> list = new ArrayList<>();

        for (Doctor doctor : doctors) {
            if (doctor.getVisits() != null || !doctor.getVisits().isEmpty()) {
                list.addAll(getListOfSlots(doctor, start, stop, modelMapper));
            } else {
                list.addAll(getListOfSlotsIfNoVisits(doctor, start, stop, modelMapper));
            }


        }
        if (list.isEmpty() || list == null) {
            throw new NoEmptySlotsException("NO_EMPTY_SLOTS_IN_TIME_RANGE_CHANGE_TIME");
        }
        return sort(list);
    }

    public List<CheckDto> getListOfSlotsIfNoVisits(Doctor doctor, LocalDateTime start, LocalDateTime stop, ModelMapper modelMapper) {
        List<LocalDateTime> slots = new ArrayList<>();
        List<CheckDto> list = new ArrayList<>();
        LocalDateTime ldt = start;
        slots.add(ldt);

        while (slots.get(slots.size() - 1).plusMinutes(120).isBefore(stop.plusMinutes(1))) {

            slots.add(ldt);
            list.add(new CheckDto(modelMapper.map(doctor, DoctorDtoCheck.class), ldt.toString()));
            ldt = ldt.plusHours(1);
        }


        return list;
    }


    public List<CheckDto> getListOfSlots(Doctor doctor, LocalDateTime start, LocalDateTime stop, ModelMapper modelMapper) {
        List<LocalDateTime> slots = new ArrayList<>();
        List<LocalDateTime> doctorVisitList = getLocalDateTimeList(doctor);
        List<CheckDto> list2 = new ArrayList<>();
        LocalDateTime ldt = start;

        slots.add(doubleChecker(start, doctorVisitList));
        if (slots.get(slots.size() - 1).plusMinutes(60).isAfter(stop)) {
            return list2;
        }
        list2.add(new CheckDto(modelMapper.map(doctor, DoctorDtoCheck.class), slots.get(slots.size() - 1).toString().replace('T', ' ')));

        while (true) {

            ldt = doubleChecker(slots.get(slots.size() - 1), doctorVisitList);
            if (ldt == slots.get(slots.size() - 1)) {
                ldt = doubleChecker(slots.get(slots.size() - 1).plusMinutes(60), doctorVisitList);
            }
            if (ldt.plusMinutes(60).isAfter(stop)) {
                break;
            }
            slots.add(ldt);
            list2.add(new CheckDto(modelMapper.map(doctor, DoctorDtoCheck.class), ldt.toString().replace('T', ' ')));

        }
        return list2;
    }


    public LocalDateTime doubleChecker(LocalDateTime start, List<LocalDateTime> list) {

        LocalDateTime slot = start;
        Collections.sort(list);

        for (LocalDateTime t : list) {
            if (isOverlapping(slot, slot.plusMinutes(60), t, t.plusMinutes(60))) {
                slot = t.plusMinutes(60);
            }
        }
        return slot;
    }


    private static boolean isAfterOrEqual(LocalDateTime date, LocalDateTime compareToDate) {
        if (date == null || compareToDate == null) {
            return false;
        }
        return date.isAfter(compareToDate) || date.isEqual(compareToDate);
    }

    @Transactional
    public ConfirmResponse deleteByToken(String token) {
        Visit visit = visitRepository.findByToken(token).orElseThrow(() -> new TokenNotFoundException("TOKEN_NOT_FOUND"));
        visitRepository.delete(visit);
        return new ConfirmResponse("VISIT_CANCELED");
    }

    public List<LocalDateTime> getLocalDateTimeList(Doctor doctor) {
        return doctor.getVisits().stream().map(s -> s.getData()).collect(Collectors.toList());
    }

    public List<CheckDto> sort(List<CheckDto> list) {
        Comparator<CheckDto> mapComparator = Comparator.comparing(CheckDto::getData);
        Collections.sort(list, mapComparator);
        return list;
    }

    public String getBaseUrl(HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        return baseUrl;
    }


}

