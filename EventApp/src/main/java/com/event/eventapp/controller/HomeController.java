package com.event.eventapp.controller;

import com.event.eventapp.model.Category;
import com.event.eventapp.model.EventType;
import com.event.eventapp.service.CategoryService;
import com.event.eventapp.service.EventTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class HomeController {
    private final EventTypeService eventTypeService;
    private final CategoryService categoryService;

    public HomeController(EventTypeService eventTypeService, CategoryService categoryService) {
        this.eventTypeService = eventTypeService;
        this.categoryService = categoryService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        List<EventType> eventTypes = eventTypeService.getAllEventTypes();
        model.addAttribute("eventTypes", eventTypes);
        return "home";
    }

    @ModelAttribute("allCategories")
    public List<Category> getAllCategories() {
        return categoryService.findAllCategories();
    }

    @GetMapping("/customer-1")
    public String customerServiceInfo(Model model) {
        return "customer-1";
    }

    @GetMapping("/customer-2")
    public String customerFacilities(Model model) {
        return "customer-1";
    }

    @GetMapping("/general-1")
    public String aboutPlanivent(Model model) {
        return "general-1";
    }

    @GetMapping("/general-2")
    public String contact(Model model) {
        return "general-2";
    }

    @GetMapping("/provider-1")
    public String listProducts(Model model) {
        return "provider-1";
    }

    @GetMapping("/provider-2")
    public String providerFacilities(Model model) {
        return "provider-2";
    }
}
