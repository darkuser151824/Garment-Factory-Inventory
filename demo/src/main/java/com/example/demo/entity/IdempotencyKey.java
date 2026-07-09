package com.example.demo.entity;

import com.example.demo.enums.IdempotencyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey extends BaseEntity {

    @Id
    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @NotNull
    @Column(name = "request_hash")
    private String requestHash;

    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Enumerated(EnumType.STRING)
    private IdempotencyStatus status;

    @Column(name = "resource_id")
    private Long resourceId;
}