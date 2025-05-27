package fr.mcoolive.crud_png.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = ImageEntity.TABLE_NAME)
public class ImageEntity {
    public static final String TABLE_NAME = "IMAGE";

    protected ImageEntity() {
        // no-args constructor required by JPA spec
    }

    public ImageEntity(final UUID id, final UUID issuerId, final byte[] content) {
        this.id = id;
        this.issuerId = issuerId;
        this.content = content;
    }

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID issuerId;

    @Column(nullable = false)
    private byte[] content;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public UUID getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(final UUID issuerId) {
        this.issuerId = issuerId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(final byte[] content) {
        this.content = content;
    }
}
