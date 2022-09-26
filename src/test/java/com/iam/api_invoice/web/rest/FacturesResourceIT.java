package com.iam.api_invoice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.iam.api_invoice.IntegrationTest;
import com.iam.api_invoice.domain.Factures;
import com.iam.api_invoice.repository.FacturesRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FacturesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FacturesResourceIT {

    private static final String DEFAULT_OBJECT = "AAAAAAAAAA";
    private static final String UPDATED_OBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CREATION_DATE = "AAAAAAAAAA";
    private static final String UPDATED_CREATION_DATE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/factures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FacturesRepository facturesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFacturesMockMvc;

    private Factures factures;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factures createEntity(EntityManager em) {
        Factures factures = new Factures().object(DEFAULT_OBJECT).description(DEFAULT_DESCRIPTION).creationDate(DEFAULT_CREATION_DATE);
        return factures;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factures createUpdatedEntity(EntityManager em) {
        Factures factures = new Factures().object(UPDATED_OBJECT).description(UPDATED_DESCRIPTION).creationDate(UPDATED_CREATION_DATE);
        return factures;
    }

    @BeforeEach
    public void initTest() {
        factures = createEntity(em);
    }

    @Test
    @Transactional
    void createFactures() throws Exception {
        int databaseSizeBeforeCreate = facturesRepository.findAll().size();
        // Create the Factures
        restFacturesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(factures)))
            .andExpect(status().isCreated());

        // Validate the Factures in the database
        List<Factures> facturesList = facturesRepository.findAll();
        assertThat(facturesList).hasSize(databaseSizeBeforeCreate + 1);
        Factures testFactures = facturesList.get(facturesList.size() - 1);
        assertThat(testFactures.getObject()).isEqualTo(DEFAULT_OBJECT);
        assertThat(testFactures.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFactures.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    void createFacturesWithExistingId() throws Exception {
        // Create the Factures with an existing ID
        factures.setId(1L);

        int databaseSizeBeforeCreate = facturesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFacturesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(factures)))
            .andExpect(status().isBadRequest());

        // Validate the Factures in the database
        List<Factures> facturesList = facturesRepository.findAll();
        assertThat(facturesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFactures() throws Exception {
        // Initialize the database
        facturesRepository.saveAndFlush(factures);

        // Get all the facturesList
        restFacturesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(factures.getId().intValue())))
            .andExpect(jsonPath("$.[*].object").value(hasItem(DEFAULT_OBJECT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE)));
    }

    @Test
    @Transactional
    void getFactures() throws Exception {
        // Initialize the database
        facturesRepository.saveAndFlush(factures);

        // Get the factures
        restFacturesMockMvc
            .perform(get(ENTITY_API_URL_ID, factures.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(factures.getId().intValue()))
            .andExpect(jsonPath("$.object").value(DEFAULT_OBJECT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE));
    }

    @Test
    @Transactional
    void getNonExistingFactures() throws Exception {
        // Get the factures
        restFacturesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFactures() throws Exception {
        // Initialize the database
        facturesRepository.saveAndFlush(factures);

        int databaseSizeBeforeUpdate = facturesRepository.findAll().size();

        // Update the factures
        Factures updatedFactures = facturesRepository.findById(factures.getId()).get();
        // Disconnect from session so that the updates on updatedFactures are not directly saved in db
        em.detach(updatedFactures);
        updatedFactures.object(UPDATED_OBJECT).description(UPDATED_DESCRIPTION).creationDate(UPDATED_CREATION_DATE);

        restFacturesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFactures.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFactures))
            )
            .andExpect(status().isOk());

        // Validate the Factures in the database
        List<Factures> facturesList = facturesRepository.findAll();
        assertThat(facturesList).hasSize(databaseSizeBeforeUpdate);
        Factures testFactures = facturesList.get(facturesList.size() - 1);
        assertThat(testFactures.getObject()).isEqualTo(UPDATED_OBJECT);
        assertThat(testFactures.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFactures.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void putNonExistingFactures() throws Exception {
        int databaseSizeBeforeUpdate = facturesRepository.findAll().size();
        factures.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacturesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, factures.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(factures))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factures in the database
        List<Factures> facturesList = facturesRepository.findAll();
        assertThat(facturesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFactures() throws Exception {
        int databaseSizeBeforeUpdate = facturesRepository.findAll().size();
        factures.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(factures))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factures in the database
        List<Factures> facturesList = facturesRepository.findAll();
        assertThat(facturesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFactures() throws Exception {
        int databaseSizeBeforeUpdate = facturesRepository.findAll().size();
        factures.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(factures)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Factures in the database
        List<Factures> facturesList = facturesRepository.findAll();
        assertThat(facturesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFacturesWithPatch() throws Exception {
        // Initialize the database
        facturesRepository.saveAndFlush(factures);

        int databaseSizeBeforeUpdate = facturesRepository.findAll().size();

        // Update the factures using partial update
        Factures partialUpdatedFactures = new Factures();
        partialUpdatedFactures.setId(factures.getId());

        partialUpdatedFactures.object(UPDATED_OBJECT).description(UPDATED_DESCRIPTION);

        restFacturesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFactures.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFactures))
            )
            .andExpect(status().isOk());

        // Validate the Factures in the database
        List<Factures> facturesList = facturesRepository.findAll();
        assertThat(facturesList).hasSize(databaseSizeBeforeUpdate);
        Factures testFactures = facturesList.get(facturesList.size() - 1);
        assertThat(testFactures.getObject()).isEqualTo(UPDATED_OBJECT);
        assertThat(testFactures.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFactures.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    void fullUpdateFacturesWithPatch() throws Exception {
        // Initialize the database
        facturesRepository.saveAndFlush(factures);

        int databaseSizeBeforeUpdate = facturesRepository.findAll().size();

        // Update the factures using partial update
        Factures partialUpdatedFactures = new Factures();
        partialUpdatedFactures.setId(factures.getId());

        partialUpdatedFactures.object(UPDATED_OBJECT).description(UPDATED_DESCRIPTION).creationDate(UPDATED_CREATION_DATE);

        restFacturesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFactures.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFactures))
            )
            .andExpect(status().isOk());

        // Validate the Factures in the database
        List<Factures> facturesList = facturesRepository.findAll();
        assertThat(facturesList).hasSize(databaseSizeBeforeUpdate);
        Factures testFactures = facturesList.get(facturesList.size() - 1);
        assertThat(testFactures.getObject()).isEqualTo(UPDATED_OBJECT);
        assertThat(testFactures.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFactures.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingFactures() throws Exception {
        int databaseSizeBeforeUpdate = facturesRepository.findAll().size();
        factures.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacturesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, factures.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(factures))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factures in the database
        List<Factures> facturesList = facturesRepository.findAll();
        assertThat(facturesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFactures() throws Exception {
        int databaseSizeBeforeUpdate = facturesRepository.findAll().size();
        factures.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(factures))
            )
            .andExpect(status().isBadRequest());

        // Validate the Factures in the database
        List<Factures> facturesList = facturesRepository.findAll();
        assertThat(facturesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFactures() throws Exception {
        int databaseSizeBeforeUpdate = facturesRepository.findAll().size();
        factures.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacturesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(factures)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Factures in the database
        List<Factures> facturesList = facturesRepository.findAll();
        assertThat(facturesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFactures() throws Exception {
        // Initialize the database
        facturesRepository.saveAndFlush(factures);

        int databaseSizeBeforeDelete = facturesRepository.findAll().size();

        // Delete the factures
        restFacturesMockMvc
            .perform(delete(ENTITY_API_URL_ID, factures.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Factures> facturesList = facturesRepository.findAll();
        assertThat(facturesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
