package unlp.info.bd2.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import unlp.info.bd2.model.Purchase;

import java.util.List;
import java.util.Optional;

@Repository
public class PurchaseRepositoryImpl implements PurchaseRepository {

    private final SessionFactory sessionFactory;

    public PurchaseRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public Purchase save(Purchase purchase) {
        return this.getSession().merge(purchase);
    }

    @Override
    public Optional<Purchase> findById(Long id) {
        return Optional.ofNullable(this.getSession().get(Purchase.class, id));
    }

    @Override
    public List<Purchase> findAll() {
        return this.getSession().createQuery("from Purchase", Purchase.class).getResultList();
    }

    @Override
    public void delete(Purchase purchase) {
        Purchase managedPurchase = purchase;
        if (!this.getSession().contains(purchase)) {
            managedPurchase = this.getSession().merge(purchase);
        }
        this.getSession().remove(managedPurchase);
    }
}

