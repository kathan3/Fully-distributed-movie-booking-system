package com.microservice.user.Models;

import jakarta.persistence.*;

@Entity
public class UserDB {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    @SequenceGenerator(name="user_id_generator",initialValue= 1 ,allocationSize = 1)
    private Integer id;
    private String name;
    @Column(unique = true)
    private String email;

    public UserDB() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserDB(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
