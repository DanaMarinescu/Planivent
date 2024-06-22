package com.event.eventapp.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private  String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "cust_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<UserTask> userTasks;

    @OneToMany(mappedBy = "sender")
    @JsonManagedReference
    private Set<Message> sentMessages;


    @OneToMany(mappedBy = "recipient")
    @JsonManagedReference
    private Set<Message> receivedMessages;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Article> articles;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private Set<Event> events;

    public User(Long id) {
        this.id = id;
    }

    public User() {
    }
}
