package com.event.eventapp.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class EventDTO {
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private OffsetDateTime date;
    private String description;
    private Long userId;

    @JsonCreator
    public EventDTO(@JsonProperty("title") String title,
                    @JsonProperty("date") OffsetDateTime date,
                    @JsonProperty("description") String description) {
        this.title = title;
        this.date = date;
        this.description = description;
    }
}
