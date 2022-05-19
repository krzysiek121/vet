package pl.kurs.vet.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class VisitSaveResponse {
    private int id;
    @JsonCreator
    public VisitSaveResponse(@JsonProperty("id")int id) {
        this.id = id;
    }
}
