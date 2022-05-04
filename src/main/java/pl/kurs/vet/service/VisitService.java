package pl.kurs.vet.service;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vet.exception.DateWrongException;
import pl.kurs.vet.exception.NoEmptySlotsException;
import pl.kurs.vet.exception.TimeVisitException;
import pl.kurs.vet.exception.TokenNotFoundException;
import pl.kurs.vet.exception.TooLateConfirmException;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.EMail;
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

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientService patientService;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;

    private final ModelMapper modelMapper;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private final EmailSenderService emailSenderService;


    @PostConstruct
    public void init() {
        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Patient p1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");

        patientRepository.saveAndFlush(p1);
        doctorRepository.saveAndFlush(l1);
        LocalDateTime time = LocalDateTime.of(2022, 12, 10, 15, 00);
        List<LocalDateTime> slots = new ArrayList<>();
        slots.add(time);
        for (int i = 0; i < 200; i++) {

            visitRepository.saveAndFlush(new Visit(l1, p1, slots.get(slots.size() - 1).plusHours(1), "token", LocalDateTime.now()));
            slots.add(slots.get(slots.size() - 1).plusHours(1));

        }
        visitRepository.deleteById(172);
        visitRepository.deleteById(189);

    }


    @Transactional
    public VisitSaveResponse save(CreateVisitCommand command) throws MessagingException {
        Doctor doctor = doctorService.findDoctorById(command.getDoctorId());
        Patient patient = patientService.findPatientById(command.getPatientId());

        //LocalDateTime timeOfVisit = LocalDateTime.parse(command.getDate(), formatter);
        LocalDateTime timeOfVisit = command.getDate();
        if (!checkOverlapping(timeOfVisit, doctor.getVisits())) {
            throw new TimeVisitException("DOCTOR_HAS_VISIT_THAT_DATE");
        }
        if (!checkOverlapping(timeOfVisit, patient.getVisits())) {
            throw new TimeVisitException("patient already has visit that date.");
        }

        Visit toSave = new Visit(doctor, patient, command.getDate(), generateNewToken(), LocalDateTime.now());

        visitRepository.saveAndFlush(toSave);
        emailSenderService.sendConfirmationToEmail(patient.getEmail(), command.getDate().toString(),
                toSave.getToken(), toSave.getPatient().getOwnerName());

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


    public static Set<LocalDateTime> getLocalDateTimesFromVisits(Set<Visit> list) {
        Set<LocalDateTime> listOfVisits = new TreeSet<>();
        for (Visit v : list) {
            listOfVisits.add(v.getData());
        }
        return listOfVisits;
    }

    @Transactional
    public List<CheckDto> check(CreateCheckVisitCommand visitCommand) {
        LocalDateTime start = visitCommand.getFrom();
        LocalDateTime stop = visitCommand.getTo();

        if (start.isAfter(stop) || start.isEqual(stop)) {
            throw new DateWrongException("WRONG_DATES_TYPED");
        }

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
         if(list.isEmpty() || list == null) {
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
        List<LocalDateTime> doctorVisitList = getLocalDateTimeFromList(doctor);
        List<CheckDto> list2 = new ArrayList<>();
        LocalDateTime ldt = start;

        slots.add(doubleChecker(start, doctorVisitList));
        if(slots.get(slots.size()-1).plusMinutes(60).isAfter(stop)){
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

    public boolean timeSlotLimit(LocalDateTime lastSlot, LocalDateTime stop) {
        System.out.println(lastSlot);
        System.out.println(stop);
        return lastSlot.plusMinutes(120).isBefore(stop.plusMinutes(1));
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

    public List<CheckDto> sort(List<CheckDto> list) {
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



    public ConfirmResponse deleteByToken(String token) {
        Visit visit = visitRepository.findByToken(token).orElseThrow(() -> new TokenNotFoundException("TOKEN_NOT_FOUND"));
        visitRepository.delete(visit);
        return new ConfirmResponse("VISIT_CANCELED");
    }

    public List<LocalDateTime> getLocalDateTimeFromList(Doctor doctor) {
        return doctor.getVisits().stream().map(s -> s.getData()).collect(Collectors.toList());
    }

}
