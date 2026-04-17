package unlp.info.bd2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.hibernate.SessionFactory;
import unlp.info.bd2.repositories.PurchaseRepository;
import unlp.info.bd2.repositories.PurchaseRepositoryImpl;
import unlp.info.bd2.repositories.ReviewRepository;
import unlp.info.bd2.repositories.ReviewRepositoryImpl;
import unlp.info.bd2.repositories.RouteRepository;
import unlp.info.bd2.repositories.RouteRepositoryImpl;
import unlp.info.bd2.repositories.ServiceRepository;
import unlp.info.bd2.repositories.ServiceRepositoryImpl;
import unlp.info.bd2.repositories.SupplierRepository;
import unlp.info.bd2.repositories.SupplierRepositoryImpl;
import unlp.info.bd2.repositories.UserRepository;
import unlp.info.bd2.repositories.UserRepositoryImpl;
import unlp.info.bd2.repositories.*;
import unlp.info.bd2.services.*;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public UserRepository createUserRepository(SessionFactory sessionFactory) {
        return new UserRepositoryImpl(sessionFactory);
    }

    @Bean
    @Primary
    public RouteRepository createRouteRepository(SessionFactory sessionFactory) {
        return new RouteRepositoryImpl(sessionFactory);
    }

    @Bean
    @Primary
    public SupplierRepository createSupplierRepository(SessionFactory sessionFactory) {
        return new SupplierRepositoryImpl(sessionFactory);
    }

    @Bean
    @Primary
    public ServiceRepository createServiceRepository(SessionFactory sessionFactory) {
        return new ServiceRepositoryImpl(sessionFactory);
    }

    @Bean
    @Primary
    public PurchaseRepository createPurchaseRepository(SessionFactory sessionFactory) {
        return new PurchaseRepositoryImpl(sessionFactory);
    }

    @Bean
    @Primary
    public ReviewRepository createReviewRepository(SessionFactory sessionFactory) {
        return new ReviewRepositoryImpl(sessionFactory);
    }

    @Bean
    @Primary
    public ToursService createService(UserRepository userRepository,
                                      RouteRepository routeRepository,
                                      SupplierRepository supplierRepository,
                                      ServiceRepository serviceRepository,
                                      PurchaseRepository purchaseRepository,
                                      ReviewRepository reviewRepository,
                                      SessionFactory sessionFactory) {
        return new ToursServiceImpl(userRepository, routeRepository, supplierRepository, serviceRepository, purchaseRepository, reviewRepository, sessionFactory);
    }
}
