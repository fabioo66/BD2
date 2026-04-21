package unlp.info.bd2.repositories;

import unlp.info.bd2.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository {

    Supplier save(Supplier supplier);

    Optional<Supplier> findById(Long id);

    List<Supplier> findAll();

    void delete(Supplier supplier);
}

