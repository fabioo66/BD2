package unlp.info.bd2.repositories;

import unlp.info.bd2.model.Purchase;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository {

    Purchase save(Purchase purchase);

    Optional<Purchase> findById(Long id);

    List<Purchase> findAll();

    void delete(Purchase purchase);
}

