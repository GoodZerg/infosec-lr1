package ru.itmo.infosec.data;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "data_items")
public class DataItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String ownerUsername;

    @Column(nullable = false, length = 80)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected DataItem() {
    }

    public DataItem(String ownerUsername, String title, String content) {
        this.ownerUsername = ownerUsername;
        this.title = title;
        this.content = content;
    }

    @PrePersist
    void setCreationTime() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

