package pl.kurs.vet.model;

import lombok.Data;

import java.util.Map;

@Data
public class EMail {

        String to;

        String from;

        String subject;

        String text;

        String template;

        Map<String, Object> properties;
}