package fr.mcoolive.crud_png.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = Issuer.TABLE_NAME)
public class Issuer {
    public static final String TABLE_NAME = "ISSUER";

    protected Issuer() {
        // no-args constructor required by JPA spec
    }

    public Issuer(final UUID id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
