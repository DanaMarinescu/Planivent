package com.event.eventapp.controller;

import com.event.eventapp.DTO.SearchResultDTO;
import com.event.eventapp.model.*;
import com.event.eventapp.repository.UserRepository;
import com.event.eventapp.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final EventTypeService eventTypeService;
    private final LocationService locationService;
    private final MessageService messageService;
    private final ReviewService reviewService;
    private final SearchService searchService;
    private  final UserRepository userRepository;
    private final EmailService emailService;

    public ProductController(ProductService productService, CategoryService categoryService, EventTypeService eventTypeService, LocationService locationService, MessageService messageService, ReviewService reviewService, SearchService searchService, UserRepository userRepository, EmailService emailService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.eventTypeService = eventTypeService;
        this.locationService = locationService;
        this.messageService = messageService;
        this.reviewService = reviewService;
        this.searchService = searchService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @ModelAttribute("allCategories")
    public List<Category> getAllCategories() {
        return categoryService.findAllCategories();
    }

    @ModelAttribute("allLocations")
    public List<Location> getAllLocations() {
        return locationService.findAllLocations();
    }

    @ModelAttribute("allEventTypes")
    public List<EventType> getAllEventTypes() {
        return eventTypeService.getAllEventTypes();
    }

    @ModelAttribute("messageForm")
    public Message messageForm() {
        return new Message();
    }

    @GetMapping
    public String showProducts(@RequestParam(value = "categoryId", required = false) Set<Long> categoryIds,
                               @RequestParam(value="eventTypeId", required = false) Set<Long> eventTypeIds,
                               @RequestParam(value = "locationId", required = false) Long locationId,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required=false) String query,
                               Model model) {
        int pageSize = 12;

        List<Category> categories = categoryService.findAllCategories();
        List<EventType> allEventTypes = eventTypeService.getAllEventTypes();
        List<Location> allLocations = locationService.findAllLocations();
        List<SearchResultDTO> searchResults = searchService.search(query);

        model.addAttribute("products", searchResults);
        model.addAttribute("eventTypes", allEventTypes);
        model.addAttribute("CategorySubcategories", categories);
        model.addAttribute("locations", allLocations);

        Page<Product> productsPage;
        if (query != null && !query.isEmpty()) {
            productsPage = productService.searchProducts(query, PageRequest.of(page, pageSize));
            model.addAttribute("query", query);
        } else {
            productsPage = getProductsByFiltering(categoryIds, eventTypeIds, locationId, page, model, pageSize, categories);
        }

        assert productsPage != null;
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("page", productsPage);
        return "services";
    }

    private Page<Product> getProductsByFiltering(Set<Long> categoryIds, Set<Long> eventTypeIds, Long locationId, int page, Model model, int pageSize, List<Category> categories) {
        Page<Product> productsPage;
        if (eventTypeIds != null && categoryIds != null && locationId != null) {
            productsPage = getProductsForAllFilters(categoryIds, eventTypeIds, locationId, page, model, pageSize);
        } else if (eventTypeIds != null && categoryIds != null) {
            productsPage = getProductsForEventTypeAndCategory(categoryIds, eventTypeIds, page, model, pageSize);
        } else if (categoryIds != null) {
            productsPage = getProductsForCategory(categoryIds, page, model, pageSize);
        } else if (eventTypeIds != null) {
            productsPage = getProductsForEvent(eventTypeIds, page, model, pageSize, categories);
        } else if (locationId != null) {
             productsPage = getProductsForLocation(locationId, page, model, pageSize);
        } else {
            model.addAttribute("errorMessage", "Filtre necorespunzătoare.");
            return null;
        }

        if (productsPage == null) {
            model.addAttribute("errorMessage", "Niciun produs găsit pentru filtrele selectate.");
            return null;
        }
        return productsPage;
    }

    private Page<Product> getProductsForEventTypeAndCategory(Set<Long> categoryIds, Set<Long> eventTypeIds, int page, Model model, int pageSize) {
        Page<Product> productsPage;
        productsPage = productService.findByCategoryAndEventType(categoryIds, eventTypeIds, PageRequest.of(page, pageSize));
        Set<Category> providedCategories = categoryService.findByIds(categoryIds);
        Set<EventType> providedEventTypes = eventTypeService.findByIds(eventTypeIds);

        String categoryNames = providedCategories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));

        String eventTypeNames = providedEventTypes.stream()
                .map(EventType::getName)
                .collect(Collectors.joining(", "));

        model.addAttribute("selectedEventTypes", eventTypeIds);
        model.addAttribute("selectedCategories", categoryIds);
        model.addAttribute("query", categoryNames + ", " +  eventTypeNames);
        return productsPage;
    }

    private Page<Product> getProductsForLocation(Long locationId, int page, Model model, int pageSize) {
        Page<Product> productsPage = productService.findByLocationId(locationId, PageRequest.of(page, pageSize));

        productsPage.forEach(product -> {
            product.setPhotos(productService.sortPhotosAlphabetically(product.getPhotos()));
        });

        String locationName = locationId != null ? locationService.findById(locationId).getAddress() : "All";

        model.addAttribute("selectedLocation", locationId);
        model.addAttribute("query",  locationName);

        return productsPage;
    }

    private Page<Product> getProductsForEvent(Set<Long> eventTypeIds, int page, Model model, int pageSize, List<Category> categories) {
        if (eventTypeIds == null || eventTypeIds.isEmpty()) {
            model.addAttribute("errorMessage", "Niciun tip de eveniment furnizat.");
            throw new IllegalArgumentException("Tipul de eveniment nu poate fi gol.");
        }

        Page<Product> productsPage = productService.findByEventTypeIds(eventTypeIds, PageRequest.of(page, pageSize));

        Set<EventType> eventTypes = eventTypeService.findByIds(eventTypeIds);
        String eventTypeNames = eventTypes.stream()
                .map(EventType::getName)
                .collect(Collectors.joining(", "));

        productsPage.forEach(product -> {
            product.setPhotos(productService.sortPhotosAlphabetically(product.getPhotos()));
        });

        model.addAttribute("selectedEventTypes", eventTypeIds);
        model.addAttribute("CategorySubcategories", categories);
        model.addAttribute("query", eventTypeNames);

        return productsPage;
    }

    private Page<Product> getProductsForCategory(Set<Long> categoryIds, int page, Model model, int pageSize) {
        Page<Product> productsPage;
        Set<Category> categories = categoryService.findByIds(categoryIds);
        productsPage = handleCategoryFiltering(categoryIds, page, pageSize, model);
        productsPage.forEach(product -> {
            product.setPhotos(productService.sortPhotosAlphabetically(product.getPhotos()));
        });

        model.addAttribute("selectedCategories", categoryIds);

        String categoryNames = categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));
        model.addAttribute("query", categoryNames);
        return productsPage;
    }

    private Page<Product> getProductsForAllFilters( Set<Long> categoryIds, Set<Long> eventTypeIds, Long locationId, int page, Model model, int pageSize) {
        Specification<Product> spec = Specification.where(null);

        if (categoryIds != null && !categoryIds.isEmpty()) {
            spec = spec.and(ProductSpecifications.hasCategory(categoryIds));
        }
        if (eventTypeIds != null && !eventTypeIds.isEmpty()) {
            spec = spec.and(ProductSpecifications.hasEventType(eventTypeIds));
        }
        if (locationId != null) {
            spec = spec.and(ProductSpecifications.hasLocation(locationId));
        }

        Page<Product> productsPage = productService.findAll(spec, PageRequest.of(page, pageSize));
        productsPage.forEach(product -> product.setPhotos(productService.sortPhotosAlphabetically(product.getPhotos())));

        Set<Category> selectedCategories = categoryService.findByIds(categoryIds);
        Set<Category> categoriesToShow = new HashSet<>();

        for (Category category : selectedCategories) {
            if (category.getParentCategory() == null) {
                categoriesToShow.addAll(categoryService.findAllCategories());
            } else {
                categoriesToShow.addAll(category.getParentCategory().getSubcategories());
            }
        }
        Set<EventType> eventTypes = eventTypeService.findByIds(eventTypeIds);
        model.addAttribute("selectedCategories", categoryIds);
        model.addAttribute("selectedEventTypes", eventTypeIds);
        model.addAttribute("CategorySubcategories", categoriesToShow);
        model.addAttribute("selectedLocation", locationId);

        String categoryNames = selectedCategories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));

        String eventTypeNames = eventTypes.stream()
                .map(EventType::getName)
                .collect(Collectors.joining(", "));

        String locationName = locationId != null ? locationService.findById(locationId).getAddress() : "Toate locaţiile";

        model.addAttribute("query", categoryNames + ", " + eventTypeNames + ", " + locationName);
        return productsPage;
    }

    private Page<Product> handleCategoryFiltering(Set<Long> categoryIds, int page, int pageSize, Model model) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            model.addAttribute("errorMessage", "Nicio categorie furnizată.");
            throw new IllegalArgumentException("Categoria nu poate fi goală.");
        }

        Set<Category> selectedCategories = categoryService.findByIds(categoryIds);

        if (selectedCategories.isEmpty()) {
            model.addAttribute("errorMessage", "Nu există categorii pentru parametrii daţi.");
            throw new IllegalStateException("Categoriile nu a putut fi găsite.");
        }

        Set<Long> allCategoryIds = new HashSet<>();
        Set<Category> subcategoriesForDisplay = new HashSet<>();

        for (Category category : selectedCategories) {
            if (category.getParentCategory() == null) {
                subcategoriesForDisplay.addAll(category.getSubcategories());
                allCategoryIds.add(category.getId());
            } else {
                allCategoryIds.add(category.getId());
                Category parent = category.getParentCategory();
                subcategoriesForDisplay.addAll(parent.getSubcategories());
            }
        }

        model.addAttribute("CategorySubcategories", subcategoriesForDisplay);
        model.addAttribute("selectedCategories", allCategoryIds);

        return productService.findByCategoryIds(new ArrayList<>(allCategoryIds), PageRequest.of(page, pageSize));
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            model.addAttribute("errorMessage", "Produsul nu a putut fi găsit.");
            return "error-page";
        }

        model.addAttribute("product", product);
        model.addAttribute("photos", product.getPhotos());
        model.addAttribute("locations", product.getLocations());
        model.addAttribute("reviews", product.getReviews());
        model.addAttribute("review", new Review());
        model.addAttribute("message", new Message());

        return "product";
    }

    @PostMapping("/submit-message")
    public String submitMessage(@ModelAttribute Message message, @RequestParam("productId") Long productId, Authentication authentication, RedirectAttributes attributes) {
        try {
            Product product = productService.findById(productId);
            User user = userRepository.findByEmail(authentication.getName());
            if (product == null) {
                attributes.addFlashAttribute("error", "Produsul nu a putut fi găsit.");
                return "redirect:/products";
            }

            message.setProduct(product);
            message.setSender(user);
            message.setRecipient(product.getUser());
            message.setStartDate(ZonedDateTime.now(ZoneId.of("Europe/Bucharest")).toLocalDateTime());
            messageService.saveMessage(message);

            emailService.sendNotificationEmail(product.getUser(), message);

            attributes.addFlashAttribute("successMessage", "Mesaj trimis cu succes!");
            return "redirect:/messages";
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Eroare la trimiterea mesajului: " + e.getMessage());
            return "redirect:/products";
        }
    }

    @PostMapping("/submit-review")
    public String submitReview(@ModelAttribute Review review, @RequestParam("productId") Long productId, Authentication authentication, RedirectAttributes attributes) {
        if (authentication != null) {
            String username = authentication.getName();
            com.event.eventapp.model.User user = userRepository.findByEmail(username);

            Product product = productService.findById(productId);
            if (product == null) {
                attributes.addFlashAttribute("error", "Produsul nu a putut fi găsit.");
                return "redirect:/products";
            }

            if (user != null) {
                review.setUser(user);
                review.setProduct(product);
                review.setDate(new Date());
                reviewService.saveReview(review);
                attributes.addFlashAttribute("successMessage", "Recenzie adăugată cu succes!");
            } else {
                attributes.addFlashAttribute("error", "Utilizator negăsit.");
                return "redirect:/error";
            }
        } else {
            attributes.addFlashAttribute("error", "Eroare de autentificare.");
            return "redirect:/login";
        }
        return "redirect:/products/product/" + productId;
    }
}
