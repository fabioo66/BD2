package unlp.info.bd2.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import unlp.info.bd2.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public User save(User user) {
        return this.getSession().merge(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(this.getSession().get(User.class, id));
    }

    @Override
    public List<User> findAll() {
        return this.getSession().createQuery("from User", User.class).getResultList();
    }

    @Override
    public void delete(User user) {
        User managedUser = user;
        if (!this.getSession().contains(user)) {
            managedUser = this.getSession().merge(user);
        }
        this.getSession().remove(managedUser);
    }
}

