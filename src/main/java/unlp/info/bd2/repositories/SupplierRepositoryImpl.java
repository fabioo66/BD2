package unlp.info.bd2.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import unlp.info.bd2.model.Supplier;

import java.util.List;
import java.util.Optional;

@Repository
public class SupplierRepositoryImpl implements SupplierRepository {

    private final SessionFactory sessionFactory;

    public SupplierRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public Supplier save(Supplier supplier) {
        return this.getSession().merge(supplier);
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        return Optional.ofNullable(this.getSession().get(Supplier.class, id));
    }

    @Override
    public List<Supplier> findAll() {
        return this.getSession().createQuery("from Supplier", Supplier.class).getResultList();
    }

    @Override
    public void delete(Supplier supplier) {
        Supplier managedSupplier = supplier;
        if (!this.getSession().contains(supplier)) {
            managedSupplier = this.getSession().merge(supplier);
        }
        this.getSession().remove(managedSupplier);
    }
}

