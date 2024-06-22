package com.event.eventapp.service;

import com.event.eventapp.DTO.SearchResultDTO;
import com.event.eventapp.model.EventType;
import com.event.eventapp.model.Product;
import com.event.eventapp.model.Photo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @PersistenceContext
    private EntityManager entityManager;

    public List<SearchResultDTO> search(String query) {
        List<SearchResultDTO> searchResults = new ArrayList<>();

        searchResults.addAll(searchInProducts(query));

        searchResults.addAll(searchInEventTypes(query));

        return searchResults;
    }

    private List<SearchResultDTO> searchInProducts(String query) {
        String jpql = "SELECT p FROM Product p WHERE p.name LIKE :query OR p.description LIKE :query";
        Query productQuery = entityManager.createQuery(jpql);
        productQuery.setParameter("query", "%" + query + "%");

        List<Product> products = productQuery.getResultList();
        List<SearchResultDTO> productResults = new ArrayList<>();
        for (Product product : products) {
            SearchResultDTO result = new SearchResultDTO();
            result.setName(product.getName());
            result.setDescription(product.getDescription());

            Set<String> imageUrls = getPhotoUrls(product);
            result.setImageUrls(new ArrayList<>(imageUrls));
            result.setType("Product");

            productResults.add(result);
        }
        return productResults;
    }

    public Set<String> getPhotoUrls(Product product) {
        if (product.getPhotos() == null) {
            return Collections.emptySet();
        }
        return product.getPhotos().stream()
                .map(Photo::getUrl)
                .collect(Collectors.toSet());
    }

    private List<SearchResultDTO> searchInEventTypes(String query) {
        String jpql = "SELECT e FROM EventType e WHERE LOWER(e.name) LIKE LOWER(:query)";
        Query eventTypeQuery = entityManager.createQuery(jpql);
        eventTypeQuery.setParameter("query", "%" + query + "%");

        List<EventType> eventTypes = eventTypeQuery.getResultList();
        List<SearchResultDTO> eventTypeResults = new ArrayList<>();
        for (EventType eventType : eventTypes) {
            SearchResultDTO result = new SearchResultDTO();
            result.setName(eventType.getName());
            result.setType("EventType");

            eventTypeResults.add(result);
        }
        return eventTypeResults;
    }
}
