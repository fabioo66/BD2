package unlp.info.bd2.repositories;

import unlp.info.bd2.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void delete(User user);
}

