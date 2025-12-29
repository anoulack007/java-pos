package com.buek.java_pos.auth.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder 
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable=false, unique=true)
    private  String username;

    @Column(name="password", nullable=false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role;

    @Column(nullable=false)
    private Boolean enabled = true;

}
