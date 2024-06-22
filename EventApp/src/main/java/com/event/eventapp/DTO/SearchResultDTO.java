package com.event.eventapp.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResultDTO {
    private String name;
    private String description;
    private List<String> imageUrls;
    private String type;
}
