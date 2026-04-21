package unlp.info.bd2.repositories;

import unlp.info.bd2.model.Route;

import java.util.List;
import java.util.Optional;

public interface RouteRepository {

    Route save(Route route);

    Optional<Route> findById(Long id);

    List<Route> findAll();

    void delete(Route route);
}

