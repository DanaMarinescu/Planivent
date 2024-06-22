package com.event.eventapp.model;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public class ProductSpecifications {
    public static Specification<Product> hasCategory(Set<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Category> categoryJoin = root.join("categories");
            return categoryJoin.get("id").in(categoryIds);
        };
    }

    public static Specification<Product> hasEventType(Set<Long> eventTypeIds) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, EventType> eventTypeJoin = root.join("eventTypes");
            return eventTypeJoin.get("id").in(eventTypeIds);
        };
    }

    public static Specification<Product> hasLocation(Long locationId) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Location> locationJoin = root.join("locations");
            return criteriaBuilder.equal(locationJoin.get("id"), locationId);
        };
    }
}
