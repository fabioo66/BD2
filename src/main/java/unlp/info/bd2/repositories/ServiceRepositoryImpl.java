package unlp.info.bd2.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import unlp.info.bd2.model.Service;

import java.util.List;
import java.util.Optional;

@Repository
public class ServiceRepositoryImpl implements ServiceRepository {

    private final SessionFactory sessionFactory;

    public ServiceRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public Service save(Service service) {
        return this.getSession().merge(service);
    }

    @Override
    public Optional<Service> findById(Long id) {
        return Optional.ofNullable(this.getSession().get(Service.class, id));
    }

    @Override
    public List<Service> findAll() {
        return this.getSession().createQuery("from Service", Service.class).getResultList();
    }

    @Override
    public void delete(Service service) {
        Service managedService = service;
        if (!this.getSession().contains(service)) {
            managedService = this.getSession().merge(service);
        }
        this.getSession().remove(managedService);
    }
}

