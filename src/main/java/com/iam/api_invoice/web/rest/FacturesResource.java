package com.iam.api_invoice.web.rest;

import com.iam.api_invoice.domain.Factures;
import com.iam.api_invoice.repository.FacturesRepository;
import com.iam.api_invoice.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.iam.api_invoice.domain.Factures}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FacturesResource {

    private final Logger log = LoggerFactory.getLogger(FacturesResource.class);

    private static final String ENTITY_NAME = "factures";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FacturesRepository facturesRepository;

    public FacturesResource(FacturesRepository facturesRepository) {
        this.facturesRepository = facturesRepository;
    }

    /**
     * {@code POST  /factures} : Create a new factures.
     *
     * @param factures the factures to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new factures, or with status {@code 400 (Bad Request)} if the factures has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/factures")
    public ResponseEntity<Factures> createFactures(@RequestBody Factures factures) throws URISyntaxException {
        log.debug("REST request to save Factures : {}", factures);
        if (factures.getId() != null) {
            throw new BadRequestAlertException("A new factures cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Factures result = facturesRepository.save(factures);
        return ResponseEntity
            .created(new URI("/api/factures/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /factures/:id} : Updates an existing factures.
     *
     * @param id the id of the factures to save.
     * @param factures the factures to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated factures,
     * or with status {@code 400 (Bad Request)} if the factures is not valid,
     * or with status {@code 500 (Internal Server Error)} if the factures couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/factures/{id}")
    public ResponseEntity<Factures> updateFactures(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Factures factures
    ) throws URISyntaxException {
        log.debug("REST request to update Factures : {}, {}", id, factures);
        if (factures.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, factures.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!facturesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Factures result = facturesRepository.save(factures);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, factures.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /factures/:id} : Partial updates given fields of an existing factures, field will ignore if it is null
     *
     * @param id the id of the factures to save.
     * @param factures the factures to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated factures,
     * or with status {@code 400 (Bad Request)} if the factures is not valid,
     * or with status {@code 404 (Not Found)} if the factures is not found,
     * or with status {@code 500 (Internal Server Error)} if the factures couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/factures/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Factures> partialUpdateFactures(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Factures factures
    ) throws URISyntaxException {
        log.debug("REST request to partial update Factures partially : {}, {}", id, factures);
        if (factures.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, factures.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!facturesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Factures> result = facturesRepository
            .findById(factures.getId())
            .map(existingFactures -> {
                if (factures.getObject() != null) {
                    existingFactures.setObject(factures.getObject());
                }
                if (factures.getDescription() != null) {
                    existingFactures.setDescription(factures.getDescription());
                }
                if (factures.getCreationDate() != null) {
                    existingFactures.setCreationDate(factures.getCreationDate());
                }

                return existingFactures;
            })
            .map(facturesRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, factures.getId().toString())
        );
    }

    /**
     * {@code GET  /factures} : get all the factures.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of factures in body.
     */
    @GetMapping("/factures")
    public List<Factures> getAllFactures() {
        log.debug("REST request to get all Factures");
        return facturesRepository.findAll();
    }

    /**
     * {@code GET  /factures/:id} : get the "id" factures.
     *
     * @param id the id of the factures to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the factures, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/factures/{id}")
    public ResponseEntity<Factures> getFactures(@PathVariable Long id) {
        log.debug("REST request to get Factures : {}", id);
        Optional<Factures> factures = facturesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(factures);
    }

    /**
     * {@code DELETE  /factures/:id} : delete the "id" factures.
     *
     * @param id the id of the factures to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/factures/{id}")
    public ResponseEntity<Void> deleteFactures(@PathVariable Long id) {
        log.debug("REST request to delete Factures : {}", id);
        facturesRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
