package com.event.eventapp.service;

import com.event.eventapp.model.*;
import com.event.eventapp.repository.ProductRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<Product> findAll(Specification<Product> spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Page<Product> findByCategoryAndEventType(Set<Long> categoryIds, Set<Long> eventTypeIds, Pageable pageable){
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            Join<Product, Category> categoryJoin = root.join("categories");
            Join<Product, EventType> eventTypeJoin = root.join("eventTypes");

            Predicate categoryPredicate = categoryJoin.get("id").in(categoryIds);
            Predicate eventTypePredicate = eventTypeJoin.get("id").in(eventTypeIds);

            return criteriaBuilder.and(categoryPredicate, eventTypePredicate);
        };

        return productRepository.findAll(spec, pageable);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Set<Photo> sortPhotosAlphabetically(Set<Photo> photos) {
        List<Photo> sortedPhotos = new ArrayList<>(photos);
        sortedPhotos.sort(Comparator.comparing(Photo::getUrl));
        return new LinkedHashSet<>(sortedPhotos);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }


    public Page<Product> findByCategoryIds(List<Long> effectiveCategoryIds, PageRequest pageable) {
        if (effectiveCategoryIds == null || effectiveCategoryIds.isEmpty()) {
            throw new IllegalArgumentException("Lista de categorii nu poate fi goală.");
        }

        return productRepository.findByCategoriesIdIn(effectiveCategoryIds, pageable);
    }

    public Page<Product> findByEventTypeIds(Set<Long> eventTypeIds, PageRequest pageable) {
        if (eventTypeIds == null || eventTypeIds.isEmpty()) {
            throw new IllegalArgumentException("Lista de tipuri de eveniment nu poate fi goală.");
        }
        return productRepository.findByEventTypeIdsIn(eventTypeIds, pageable);
    }

    public Page<Product> findByLocationId(Long locationId, PageRequest pageable) {
        if (locationId == null) {
            throw new IllegalArgumentException("Locaţia nu poate fi goală.");
        }

        return productRepository.findByLocationIdIn(locationId, pageable);
    }

    public Page<Product> searchProducts(String query, Pageable pageable) {
        return productRepository.findByQuery(query, pageable);
    }
}
