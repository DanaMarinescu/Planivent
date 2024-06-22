package com.event.eventapp.repository;

import com.event.eventapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findByCategoriesIdIn(List<Long> effectiveCategoryIds, PageRequest pageable);

    @Query("SELECT DISTINCT p FROM Product p JOIN p.eventTypes e WHERE e.id IN :eventTypeIds")
    Page<Product> findByEventTypeIdsIn(@Param("eventTypeIds") Set<Long> eventTypeIds, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p JOIN p.locations loc WHERE loc.id = :locationId")
    Page<Product> findByLocationIdIn(@Param("locationId") Long locationId,  Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.categories c " +
            "LEFT JOIN p.eventTypes e " +
            "LEFT JOIN p.locations l " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(l.address) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Product> findByQuery(@Param("query") String query, Pageable pageable);

    @EntityGraph(attributePaths = {"eventTypes", "locations", "photos"})
    Optional<Product> findById(Long id);
}
