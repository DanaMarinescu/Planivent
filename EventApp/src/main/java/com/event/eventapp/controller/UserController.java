package com.event.eventapp.controller;

import com.event.eventapp.DTO.ProductDTO;
import com.event.eventapp.model.*;
import com.event.eventapp.repository.RoleRepository;
import com.event.eventapp.repository.UserRepository;
import com.event.eventapp.service.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final EventTypeService eventTypeService;
    private final LocationService locationService;
    private final DefaultUserService defaultUserService;
    private final PhotoService photoService;

    public UserController(UserRepository userRepository, RoleRepository roleRepository, ProductService productService,
                          CategoryService categoryService, EventTypeService eventTypeService, LocationService locationService,
                          @Qualifier("defaultUserService") DefaultUserService defaultUserService, PhotoService photoService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.productService = productService;
        this.categoryService = categoryService;
        this.eventTypeService = eventTypeService;
        this.locationService = locationService;
        this.defaultUserService = defaultUserService;
        this.photoService = photoService;
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

    @ModelAttribute("allSubcategories")
    public List<Category> getAllSubcategories() {
        return categoryService.findAllSubcategories();
    }

    @Secured("Provider")
    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername());
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("roles", roleRepository.findAll());
                return "userProfile";
            } else {
                model.addAttribute("error", "Utilizator negăsit!");
                return "error-page";
            }
        } else {
            model.addAttribute("error", "Eroare de încărcare a profilului!");
            return "error-page";
        }
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User updatedUser, RedirectAttributes redirectAttributes) {
        User existingUser = userRepository.findById(updatedUser.getId()).orElse(null);
        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setPhone(updatedUser.getPhone());
            userRepository.save(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "Modificările au fost aplicate cu succes!");
            return "redirect:/user/profile";
        } else {
            redirectAttributes.addFlashAttribute("error", "Utilizator negăsit!");
            return "redirect:/error-page";
        }
    }

    @GetMapping("/products")
    public String getProductsPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername());

                if (user != null) {
                    List<Product> products = user.getProducts();
                    List<ProductDTO> productDTOs = products.stream()
                            .map(ProductDTO::new)
                            .collect(Collectors.toList());
                    model.addAttribute("products", productDTOs);
                    return "my-products";
                } else {
                    model.addAttribute("error", "Utilizator negăsit!");
                    return "error-page";
                }
        } else {
            model.addAttribute("error", "Eroare de încărcare a profilului!");
            return "error-page";
        }
    }

    @GetMapping("/products/subcategories")
    public ResponseEntity<?> getSubcategoriesByCategoryIds(@RequestParam (required = false) List<Long> categoryIds) {
        try {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Category> subcategories = new ArrayList<>();
            for (Long categoryId : categoryIds) {
                List<Category> subs = categoryService.findSubcategoriesByCategoryId(categoryId);
                subcategories.addAll(subs);
            }
            return ResponseEntity.ok(subcategories);
        } catch (DataAccessException dae) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/products/subcategories/ids")
    public ResponseEntity<List<Long>> getSubcategoryIds() {
        List<Long> subcategoryIds = categoryService.findSubcategories()
                .stream()
                .map(Category::getId)
                .collect(Collectors.toList());
        return ResponseEntity.ok(subcategoryIds);
    }

    @PostMapping("/products/new-product")
    public ResponseEntity<?> createProduct(@ModelAttribute ProductDTO productDTO, @RequestParam("photos") MultipartFile[] photos, Principal principal) {
        if (productDTO == null) {
            return ResponseEntity.badRequest().body("Datele produsului lipsesc.");
        }

        if (productDTO.getUserId() == null && principal != null) {
            User user = defaultUserService.findByName(principal.getName());
            productDTO.setUserId(user.getId());
        }

        try {
            Product product = convertToProduct(productDTO);

            handlePhotoUploadAndLink(product, photos);
            populateProductRelations(product, productDTO);
            Product savedProduct = productService.saveProduct(product);

            return ResponseEntity.ok(new ProductDTO(savedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la crearea produsului: " + e.getMessage());
        }
    }

    private void handlePhotoUploadAndLink(Product product, MultipartFile[] photos) {
        if (photos != null && photos.length > 0) {
            Set<Photo> uploadedPhotos = photoService.storeFiles(photos, product);
            product.setPhotos(uploadedPhotos);
        }
    }

    private void populateProductRelations(Product product, ProductDTO dto) {
        Set<Category> categories = categoryService.findByIds(dto.getCategoryIds());
        Set<Category> subcategories = categoryService.findByIds(dto.getSubcategoryIds());
        Set<Location> locations = locationService.findByIds(dto.getLocationIds());
        Set<EventType> eventTypes = eventTypeService.findByIds(dto.getEventTypeIds());

        categories.addAll(subcategories);
        product.setCategories(categories);
        product.setLocations(locations);
        product.setEventTypes(eventTypes);
    }

    public Product convertToProduct(ProductDTO productDTO) {
        Product product = new Product();

        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setAddress(productDTO.getAddress());
        product.setPhone(productDTO.getPhone());
        product.setMail(productDTO.getMail());

        if (productDTO.getUserId() != null) {
            User user = defaultUserService.findById(productDTO.getUserId());
            product.setUser(user);
        }

        return product;
    }

    @PostMapping("/products/edit-product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @ModelAttribute ProductDTO productDTO, @RequestParam("photos") MultipartFile[] photos) {
        try {
            Product product = productService.findById(id);

            if (product == null) {
                return ResponseEntity.notFound().build();
            }

            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setAddress(productDTO.getAddress());
            product.setPhone(productDTO.getPhone());
            product.setMail(productDTO.getMail());

            if (productDTO.getEventTypeIds() != null && !productDTO.getEventTypeIds().isEmpty()) {
                Set<EventType> eventTypes = eventTypeService.findByIds(productDTO.getEventTypeIds());
                product.setEventTypes(eventTypes);
            }

            if (productDTO.getLocationIds() != null && !productDTO.getLocationIds().isEmpty()) {
                Set<Location> locations = locationService.findByIds(productDTO.getLocationIds());
                product.setLocations(locations);
            }

            if (photos != null && photos.length > 0) {
                Set<Photo> uploadedPhotos = photoService.storeFiles(photos, product);
                product.getPhotos().addAll(uploadedPhotos);
            }

            Product updatedProduct = productService.saveProduct(product);
            return ResponseEntity.ok(new ProductDTO(updatedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la actualizarea produsului: " + e.getMessage());
        }
    }

    @DeleteMapping("/products/delete-product/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Produs şters cu succes!", "id", id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Eroare la ştergerea produsului."));
        }
    }
}
