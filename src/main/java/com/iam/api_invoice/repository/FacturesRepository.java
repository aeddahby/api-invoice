package com.iam.api_invoice.repository;

import com.iam.api_invoice.domain.Factures;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Factures entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FacturesRepository extends JpaRepository<Factures, Long> {}
