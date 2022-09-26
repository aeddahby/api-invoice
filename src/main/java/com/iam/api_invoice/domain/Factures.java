package com.iam.api_invoice.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A Factures.
 */
@Entity
@Table(name = "factures")
public class Factures implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "object")
    private String object;

    @Column(name = "description")
    private String description;

    @Column(name = "creation_date")
    private String creationDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Factures id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObject() {
        return this.object;
    }

    public Factures object(String object) {
        this.setObject(object);
        return this;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getDescription() {
        return this.description;
    }

    public Factures description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public Factures creationDate(String creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Factures)) {
            return false;
        }
        return id != null && id.equals(((Factures) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Factures{" +
            "id=" + getId() +
            ", object='" + getObject() + "'" +
            ", description='" + getDescription() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            "}";
    }
}
