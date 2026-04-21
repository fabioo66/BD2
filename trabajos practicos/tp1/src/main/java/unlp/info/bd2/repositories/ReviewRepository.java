package unlp.info.bd2.repositories;

import unlp.info.bd2.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Review save(Review review);

    Optional<Review> findById(Long id);

    List<Review> findAll();

    void delete(Review review);
}

