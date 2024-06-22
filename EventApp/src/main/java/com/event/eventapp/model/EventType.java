package com.event.eventapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "eventtype")
@Getter
@Setter
@NoArgsConstructor
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String image;

    @ManyToMany(mappedBy = "eventTypes", fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();
}
