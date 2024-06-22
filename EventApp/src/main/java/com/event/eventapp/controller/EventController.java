package com.event.eventapp.controller;

import com.event.eventapp.DTO.EventDTO;
import com.event.eventapp.model.Category;
import com.event.eventapp.model.Event;
import com.event.eventapp.model.User;
import com.event.eventapp.repository.RoleRepository;
import com.event.eventapp.repository.UserRepository;
import com.event.eventapp.service.CategoryService;
import com.event.eventapp.service.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class EventController {

    private final EventService eventService;
    private final CategoryService categoryService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public EventController(EventService eventService, CategoryService categoryService, RoleRepository roleRepository, UserRepository userRepository) {
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @ModelAttribute("allCategories")
    public List<Category> getAllCategories() {
        return categoryService.findAllCategories();
    }

    @GetMapping("/calendar")
    public String calendar(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername());
        if (user != null) {
            List<Event> events = eventService.getEventsByUserId(user.getId());
            model.addAttribute("events", events);
            model.addAttribute("userId", user.getId());
            model.addAttribute("user", user);
            model.addAttribute("roles", roleRepository.findAll());
            return "calendar";
        } else {
            model.addAttribute("error", "Utilizator negăsit!");
            return "error-page";
        }
    }

    @GetMapping("/events/fetch")
    public ResponseEntity<?> getEvents( Principal principal) {
        try {
            User user = userRepository.findByEmail(principal.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Utilizator negăsit.");
            }

            List<Event> events = eventService.getAllEventsByUserId(user.getId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

            List<Map<String, Object>> response = events.stream().map(event -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", event.getId());
                map.put("title", event.getTitle());
                String formattedDateTime = event.getDateTime()
                        .atZone(ZoneId.systemDefault())
                        .withZoneSameInstant(ZoneId.of("Europe/Bucharest"))
                        .format(formatter);
                map.put("dateTime", formattedDateTime);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Format incorect pentru dată: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("A apărut o eroare la încărcare: " + e.getMessage());
        }
    }

    @PostMapping("/events/add")
    public ResponseEntity<?> addEvent(@RequestBody String rawJson, Principal principal) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            String title = root.path("title").asText();
            String description = root.path("description").asText();
            String dateString = root.path("dateTime").asText();

            OffsetDateTime date = OffsetDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            EventDTO eventDTO = new EventDTO(title, date, description);
            Event event = eventService.addEvent(eventDTO, principal.getName());
            return ResponseEntity.ok(event);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Format incorect pentru dată: " + e.getMessage());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Eroare la conversia JSON: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("A apărut o eroare la adăugare: " + e.getMessage());
        }
    }

    @DeleteMapping("/events/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.ok(Map.of("message", "Eveniment şters cu succes: ", "id", id));
        } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Eroare la ştergearea evenimentului."));
    }
    }

}
