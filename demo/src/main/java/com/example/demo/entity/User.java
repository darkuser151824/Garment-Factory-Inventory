package com.example.demo.entity;

import com.example.demo.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true,nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

}
