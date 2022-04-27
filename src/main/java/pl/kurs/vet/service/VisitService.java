package pl.kurs.vet.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vet.exception.DateWrongException;
import pl.kurs.vet.exception.TimeVisitException;
import pl.kurs.vet.exception.TokenNotFoundException;
import pl.kurs.vet.exception.TooLateConfirmException;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.Patient;
import pl.kurs.vet.model.Visit;
import pl.kurs.vet.model.dto.CheckDto;
import pl.kurs.vet.model.dto.DoctorDtoCheck;
import pl.kurs.vet.repository.DoctorReposirtory;
import pl.kurs.vet.repository.VisitRepository;
import pl.kurs.vet.request.CreateCheckVisitCommand;
import pl.kurs.vet.request.CreateVisitCommand;
import pl.kurs.vet.response.ConfirmResponse;
import pl.kurs.vet.response.VisitSaveResponse;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final VisitRepository visitRepository;
    private final DoctorReposirtory doctorReposirtory;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final JavaMailSender javaMailSender;
    private final ModelMapper modelMapper;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();


    @Transactional
    public VisitSaveResponse save(CreateVisitCommand command) {
        Doctor doctor = doctorService.findDoctorById(command.getDoctorId());
        Patient patient = patientService.findPatientById(command.getPatientId());

        LocalDateTime timeOfVisit = LocalDateTime.parse(command.getData(), formatter);

        if (!checkOverlapping(timeOfVisit, doctor.getVisits())) {
            throw new TimeVisitException("doctor already has visit that date.");
        }
        if (!checkOverlapping(timeOfVisit, patient.getVisits())) {
            throw new TimeVisitException("patient already has visit that date.");
        }

        Visit toSave = new Visit(doctor, patient, timeOfVisit, generateNewToken(), LocalDateTime.now());

        sendConfirmationToEmail(patient.getEmail(), command.getData(), toSave.getToken());
        visitRepository.saveAndFlush(toSave);

        return new VisitSaveResponse(toSave.getId());
    }

    @Transactional
    public ConfirmResponse confirm(String token) throws Exception {

        Visit visit = visitRepository.findByToken(token).orElseThrow(() -> new TokenNotFoundException("TOKEN_NOT_FOUND"));


        if (LocalDateTime.now().isBefore(visit.getTimeSendConfirmation().plusMinutes(2))) {

            visit.setConfirmed(true);

            return new ConfirmResponse("Wizyta Potwierdzona !!");
        } else {
            throw new TooLateConfirmException("too late");
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
            for (LocalDateTime t : getLocalDateTimesFromVisits(list)) {
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


    public static Set<LocalDateTime> getLocalDateTimesFromVisits(Set<Visit> list) {
        Set<LocalDateTime> listOfVisits = new TreeSet<>();
        for (Visit v : list) {
            listOfVisits.add(v.getData());
        }
        return listOfVisits;
    }


    public List<CheckDto> check(CreateCheckVisitCommand visitCommand) {
        LocalDateTime start = LocalDateTime.parse(visitCommand.getDate_from(), formatter);
        LocalDateTime stop = LocalDateTime.parse(visitCommand.getDate_to(), formatter);

        if (start.isAfter(stop) || start.isEqual(stop)) {
            throw new DateWrongException("WRONG_DATES_TYPED");
        }

        List<Doctor> list = new ArrayList<>();
        for (Doctor d : doctorReposirtory.findAll()) {
            if (d.getType().equals(visitCommand.getType()) && d.getAnimalType().equals(visitCommand.getAnimal())) {
                list.add(d);
            }
        }
        return getSlots(list, start, stop, modelMapper);

    }

    public static List<CheckDto> getSlots(List<Doctor> doctors, LocalDateTime start, LocalDateTime stop, ModelMapper modelMapper) {
        List<CheckDto> list = new ArrayList<>();
        List<LocalDateTime> slots;
        for (Doctor d : doctors) {
            if (d.getVisits() != null || !d.getVisits().isEmpty()) {
                list.addAll(getListOfSlots(d, start, stop, modelMapper));
            } else {
                list.addAll(getListOfSlotsIfNoVisits(d, start, stop, modelMapper));
            }


        }

        return sort(list);
    }

    public static List<CheckDto> getListOfSlotsIfNoVisits(Doctor doctor, LocalDateTime start, LocalDateTime stop, ModelMapper modelMapper) {
        List<LocalDateTime> slots = new ArrayList<>();
        List<CheckDto> list = new ArrayList<>();
        LocalDateTime ldt = start;
        while (slots.get(slots.size() - 1).plusMinutes(121).isBefore(stop.plusMinutes(1))) {

            slots.add(ldt);
            list.add(new CheckDto(modelMapper.map(doctor, DoctorDtoCheck.class), ldt.toString()));
            ldt = ldt.plusHours(1);
        }


        return list;
    }

    public static List<CheckDto> getListOfSlots(Doctor doctor, LocalDateTime start, LocalDateTime stop, ModelMapper modelMapper) {
        List<CheckDto> list = new ArrayList<>();
        List<LocalDateTime> listOfDoctorVisits = getLocalDateTimeFromList(doctor);
        for (LocalDateTime timeSlot : time(start, stop, listOfDoctorVisits)) {
            list.add(new CheckDto(modelMapper.map(doctor, DoctorDtoCheck.class), timeSlot.toString().replace('T', ' ')));
        }
        //System.out.println(list);

        return list;
    }

    public static List<LocalDateTime> time(LocalDateTime start, LocalDateTime stop, List<LocalDateTime> list) {
        List<LocalDateTime> slots = new ArrayList<>();
        List<LocalDateTime> whiteList = new ArrayList<>();
        LocalDateTime ldt = start;
        slots.add(doubleChecker(start, list));

        while (slots.get(slots.size() - 1).plusMinutes(121).isBefore(stop.plusMinutes(1))) {

            ldt = doubleChecker(slots.get(slots.size() - 1), list);
            if (ldt == slots.get(slots.size() - 1)) {
                ldt = doubleChecker(slots.get(slots.size() - 1).plusMinutes(60), list);
            }
            slots.add(ldt);

        }
        return slots;
    }

    public static LocalDateTime doubleChecker(LocalDateTime start, List<LocalDateTime> list) {
        LocalDateTime slot = start;
        Collections.sort(list);
        for (LocalDateTime t : list) {
            if (isOverlapping(slot, slot.plusMinutes(60), t, t.plusMinutes(60))) {
                slot = t.plusMinutes(60);
            }
        }
        return slot;
    }

    public static List<CheckDto> sort(List<CheckDto> list) {
        Comparator<CheckDto> mapComparator = Comparator.comparing(CheckDto::getData);
        Collections.sort(list, mapComparator);
        return list;
    }

    private static boolean isAfterOrEqual(LocalDateTime date, LocalDateTime compareToDate) {
        if (date == null || compareToDate == null) {
            return false;
        }
        return date.isAfter(compareToDate) || date.isEqual(compareToDate);
    }

    private static boolean isBeforeOrEqual(LocalDateTime date, LocalDateTime compareToDate) {
        if (date == null || compareToDate == null) {
            return false;
        }
        return date.isBefore(compareToDate) || date.isEqual(compareToDate);
    }
    @Async
    public void sendConfirmationToEmail(String PatientEmail, String data, String token) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(PatientEmail);
        msg.setFrom("awandaliak@gmail.com");
        msg.setSubject("Visit confirmation " + data);
        msg.setText("Please confirm You visit, click to the link : http://localhost:8080/visit/confirm/" + token);
        javaMailSender.send(msg);
    }

    public ConfirmResponse deleteByToken(String token) {
        Visit visit = visitRepository.findByToken(token).orElseThrow(() -> new TokenNotFoundException("TOKEN_NOT_FOUND"));

            visitRepository.delete(visit);


        return new ConfirmResponse("VISIT_CANCELED");
    }

    public static List<LocalDateTime> getLocalDateTimeFromList(Doctor doctor) {
        return doctor.getVisits().stream().map(s -> s.getData()).collect(Collectors.toList());
    }

}

