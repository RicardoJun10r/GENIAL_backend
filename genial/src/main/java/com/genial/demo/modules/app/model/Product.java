package com.genial.demo.modules.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "products")
@SQLDelete(sql = "UPDATE products SET active = false WHERE id=?")
@SQLRestriction("active = true")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_storage")
    private Storage storage;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String description;
    
    private String sector;

    @Column(precision = 19, scale = 4)
    private BigDecimal value;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private boolean active = true;

    public Product(String name, String description, String sector, BigDecimal value, Integer quantidade) {
        this.name = name;
        this.description = description;
        this.sector = sector;
        this.value = value;
        this.quantidade = quantidade;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Product product)) {
            return false;
        }
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
