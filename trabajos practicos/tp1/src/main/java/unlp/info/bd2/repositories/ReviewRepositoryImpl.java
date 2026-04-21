package unlp.info.bd2.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import unlp.info.bd2.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepositoryImpl implements ReviewRepository {

    private final SessionFactory sessionFactory;

    public ReviewRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public Review save(Review review) {
        return this.getSession().merge(review);
    }

    @Override
    public Optional<Review> findById(Long id) {
        return Optional.ofNullable(this.getSession().get(Review.class, id));
    }

    @Override
    public List<Review> findAll() {
        return this.getSession().createQuery("from Review", Review.class).getResultList();
    }

    @Override
    public void delete(Review review) {
        Review managedReview = review;
        if (!this.getSession().contains(review)) {
            managedReview = this.getSession().merge(review);
        }
        this.getSession().remove(managedReview);
    }
}

