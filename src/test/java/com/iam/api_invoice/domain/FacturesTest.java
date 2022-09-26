package com.iam.api_invoice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.iam.api_invoice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FacturesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Factures.class);
        Factures factures1 = new Factures();
        factures1.setId(1L);
        Factures factures2 = new Factures();
        factures2.setId(factures1.getId());
        assertThat(factures1).isEqualTo(factures2);
        factures2.setId(2L);
        assertThat(factures1).isNotEqualTo(factures2);
        factures1.setId(null);
        assertThat(factures1).isNotEqualTo(factures2);
    }
}
