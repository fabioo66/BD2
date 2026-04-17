package unlp.info.bd2.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import unlp.info.bd2.model.Route;

import java.util.List;
import java.util.Optional;

@Repository
public class RouteRepositoryImpl implements RouteRepository {

    private final SessionFactory sessionFactory;

    public RouteRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public Route save(Route route) {
        return this.getSession().merge(route);
    }

    @Override
    public Optional<Route> findById(Long id) {
        return Optional.ofNullable(this.getSession().get(Route.class, id));
    }

    @Override
    public List<Route> findAll() {
        return this.getSession().createQuery("from Route", Route.class).getResultList();
    }

    @Override
    public void delete(Route route) {
        Route managedRoute = route;
        if (!this.getSession().contains(route)) {
            managedRoute = this.getSession().merge(route);
        }
        this.getSession().remove(managedRoute);
    }
}

