package com.event.eventapp.service;

import com.event.eventapp.model.Category;
import com.event.eventapp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAllCategories() {
        return categoryRepository.findTopLevelCategories();
    }

    public List<Category> findAllSubcategories() {
        return categoryRepository.findAllSubcategories();
    }

    public Category findById(Long categoryId) {
        return categoryRepository.findCategoryById(categoryId);
    }

    public List<Category> findSubcategoriesByCategoryId(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            return category.getSubcategories().stream()
                    .filter(subcategory -> subcategory.getParentCategory() != null)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public Set<Category> findByIds(Set<Long> categoryIds) {
        return new HashSet<>(categoryRepository.findAllById(categoryIds));
    }

    public List<Category> findSubcategories() {
        return categoryRepository.findAllSubcategories();
    }
}
