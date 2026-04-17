package unlp.info.bd2.repositories;

import unlp.info.bd2.model.Service;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository {

    Service save(Service service);

    Optional<Service> findById(Long id);

    List<Service> findAll();

    void delete(Service service);
}

