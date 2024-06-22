package com.event.eventapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
@Entity
@Table(name="location")
@Getter
@Setter
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String address;

    @ManyToMany(mappedBy = "locations")
    private Set<Product> products;

    public Location(Long id) {
        this.id = id;
    }

}
