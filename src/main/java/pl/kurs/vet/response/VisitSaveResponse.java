package pl.kurs.vet.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
public class VisitSaveResponse {
    private int id;
    @JsonCreator
    public VisitSaveResponse(@JsonProperty("id")int id) {
        this.id = id;
    }
}
