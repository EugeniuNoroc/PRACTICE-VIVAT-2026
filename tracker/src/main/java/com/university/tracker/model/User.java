package com.university.tracker.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // TODO (W3 review): username/email nullable и без unique — допустим пользователь без email и дубли email.
    //   Если нужны инварианты — @Column(nullable = false, unique = true) + совпадающий DDL в миграции.
    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "assignee", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Task> assignedTasks =  new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        // Не Object.hash(id) потому что id у новой энтити равен null до вызова em.persist() из-за чего объект физически есть в Set, но найти невозмжно после генерации нового UUID при em.persist()
        return getClass().hashCode();
    }
}
