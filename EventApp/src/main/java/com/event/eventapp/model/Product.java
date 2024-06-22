package com.event.eventapp.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="product")
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String phone;
    private String mail;
    private String address;
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "product_location",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private Set<Location> locations = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "productcategory",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @BatchSize(size = 10)
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Photo> photos = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "producteventtype",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "event_type_id")
    )
    private Set<EventType> eventTypes = new HashSet<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Message> messages;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    private List<Review> reviews;

    public void setLocations(Set<Location> locations) {
        this.locations.clear();
        if (locations != null) {
            this.locations.addAll(locations);
        }
    }

    public void setCategories(Set<Category> categories) {
        this.categories.clear();
        if (categories != null) {
            categories.forEach(this::addCategory);
        }
    }

    public void addCategory(Category category) {
        categories.add(category);
        category.getProducts().add(this);
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos.clear();
        if (photos != null) {
            this.photos.addAll(photos);
        }
    }

    public void setEventTypes(Set<EventType> eventTypes) {
        this.eventTypes.clear();
        if (eventTypes != null) {
            this.eventTypes.addAll(eventTypes);
        }
    }
}
