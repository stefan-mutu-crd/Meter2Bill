package com.cedacri.internship.repositories;

import com.cedacri.internship.entities.contract.finance.Invoice;
import com.cedacri.internship.repositories.specification.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends BaseRepository<Invoice, Long> {

    boolean existsByContractId(Long contractId);
}
