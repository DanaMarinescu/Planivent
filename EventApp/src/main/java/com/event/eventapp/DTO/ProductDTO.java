package com.event.eventapp.DTO;

import com.event.eventapp.model.Photo;
import com.event.eventapp.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String address;
    private String phone;
    private String mail;
    private Long userId;
    private List<MultipartFile> photos;
    private String categoryIds;
    private String subcategoryIds;
    private String locationIds;
    private String photoUrls;
    private String eventTypeIds;

    public Set<Long> getEventTypeIds() {
        return parseIds(eventTypeIds);
    }

    public Set<Long> getCategoryIds() {
        return parseIds(categoryIds);
    }


    public Set<Long> getSubcategoryIds() {
        return parseIds(subcategoryIds);
    }

    public Set<Long> getLocationIds() {
        return parseIds(locationIds);
    }

    public ProductDTO(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.address = product.getAddress();
        this.phone = product.getPhone();
        this.mail = product.getMail();
        this.userId = product.getUser().getId();

        this.categoryIds = product.getCategories().stream()
                .map(category -> category.getId().toString())
                .collect(Collectors.joining(","));
        this.subcategoryIds = product.getCategories().stream()
                .filter(category -> category.getParentCategory() != null && category.getParentCategory().getId() == 1)
                .map(category -> category.getId().toString())
                .collect(Collectors.joining(","));
        this.locationIds = product.getLocations().stream()
                .map(location -> location.getId().toString())
                .collect(Collectors.joining(","));
        this.photoUrls = product.getPhotos().stream()
                .map(Photo::getUrl)
                .collect(Collectors.joining(","));
        this.eventTypeIds = product.getEventTypes().stream()
                .map(eventType -> eventType.getId().toString())
                .collect(Collectors.joining(","));
    }

    public Set<Long> parseIds(String ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }
}
