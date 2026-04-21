package unlp.info.bd2.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;
import unlp.info.bd2.model.*;
import unlp.info.bd2.repositories.PurchaseRepository;
import unlp.info.bd2.repositories.ReviewRepository;
import unlp.info.bd2.repositories.RouteRepository;
import unlp.info.bd2.repositories.ServiceRepository;
import unlp.info.bd2.repositories.SupplierRepository;
import unlp.info.bd2.repositories.UserRepository;
import unlp.info.bd2.utils.ToursException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class ToursServiceImpl implements ToursService {

    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final SupplierRepository supplierRepository;
    private final ServiceRepository serviceRepository;
    private final PurchaseRepository purchaseRepository;
    private final ReviewRepository reviewRepository;
    private final SessionFactory sessionFactory;

    public ToursServiceImpl(UserRepository userRepository,
                            RouteRepository routeRepository,
                            SupplierRepository supplierRepository,
                            ServiceRepository serviceRepository,
                            PurchaseRepository purchaseRepository,
                            ReviewRepository reviewRepository,
                            SessionFactory sessionFactory) {
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.supplierRepository = supplierRepository;
        this.serviceRepository = serviceRepository;
        this.purchaseRepository = purchaseRepository;
        this.reviewRepository = reviewRepository;
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    private <T> T orThrow(Optional<T> optional, String message) throws ToursException {
        return optional.orElseThrow(() -> new ToursException(message));
    }

    private boolean hasAssignedRoutes(User user) {
        if (user instanceof DriverUser driverUser) {
            return driverUser.getRoutes() != null && !driverUser.getRoutes().isEmpty();
        }
        if (user instanceof TourGuideUser tourGuideUser) {
            return tourGuideUser.getRoutes() != null && !tourGuideUser.getRoutes().isEmpty();
        }
        return false;
    }

    private void copyBaseUserData(User target, User source) {
        target.setPassword(source.getPassword());
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setBirthdate(source.getBirthdate());
        target.setPhoneNumber(source.getPhoneNumber());
    }

    @Override
    public User createUser(String username, String password, String fullName, String email, Date birthdate, String phoneNumber) throws ToursException {
        if (this.getUserByUsername(username).isPresent()) {
            throw new ToursException("Constraint Violation");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(fullName);
        user.setEmail(email);
        user.setBirthdate(birthdate);
        user.setPhoneNumber(phoneNumber);
        user.setActive(true);
        this.getSession().persist(user);
        return user;
    }

    @Override
    public DriverUser createDriverUser(String username, String password, String fullName, String email, Date birthdate, String phoneNumber, String expedient) throws ToursException {
        if (this.getUserByUsername(username).isPresent()) {
            throw new ToursException("Constraint Violation");
        }
        DriverUser user = new DriverUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(fullName);
        user.setEmail(email);
        user.setBirthdate(birthdate);
        user.setPhoneNumber(phoneNumber);
        user.setExpedient(expedient);
        user.setActive(true);
        this.getSession().persist(user);
        return user;
    }

    @Override
    public TourGuideUser createTourGuideUser(String username, String password, String fullName, String email, Date birthdate, String phoneNumber, String education) throws ToursException {
        if (this.getUserByUsername(username).isPresent()) {
            throw new ToursException("Constraint Violation");
        }
        TourGuideUser user = new TourGuideUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(fullName);
        user.setEmail(email);
        user.setBirthdate(birthdate);
        user.setPhoneNumber(phoneNumber);
        user.setEducation(education);
        user.setActive(true);
        this.getSession().persist(user);
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) throws ToursException {
        return this.userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) throws ToursException {
        return this.userRepository.findAll().stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst();
    }

    @Override
    public User updateUser(User user) throws ToursException {
        User persistedUser = this.orThrow(this.userRepository.findById(user.getId()), "No existe el usuario");
        String username = persistedUser.getUsername();
        boolean active = persistedUser.isActive();

        this.copyBaseUserData(persistedUser, user);
        persistedUser.setUsername(username);
        persistedUser.setActive(active);

        if (persistedUser instanceof DriverUser persistedDriver && user instanceof DriverUser driverUser) {
            persistedDriver.setExpedient(driverUser.getExpedient());
        }
        if (persistedUser instanceof TourGuideUser persistedGuide && user instanceof TourGuideUser guideUser) {
            persistedGuide.setEducation(guideUser.getEducation());
        }

        return this.userRepository.save(persistedUser);
    }

    @Override
    public void deleteUser(User user) throws ToursException {
        User persistedUser = this.orThrow(this.userRepository.findById(user.getId()), "No existe el usuario");

        if (!persistedUser.isActive()) {
            throw new ToursException("El usuario se encuentra desactivado");
        }

        if (this.hasAssignedRoutes(persistedUser)) {
            throw new ToursException("El usuario no puede ser desactivado");
        }

        if (persistedUser.getPurchaseList() == null || persistedUser.getPurchaseList().isEmpty()) {
            this.userRepository.delete(persistedUser);
        } else {
            persistedUser.setActive(false);
            this.userRepository.save(persistedUser);
        }
    }

    @Override
    public Stop createStop(String name, String description) throws ToursException {
        Stop stop = new Stop();
        stop.setName(name);
        stop.setDescription(description);
        this.getSession().persist(stop);
        return stop;
    }

    @Override
    public List<Stop> getStopByNameStart(String name) {
        return this.getSession().createQuery("from Stop where name like :name", Stop.class)
                .setParameter("name", name + "%")
                .getResultList();
    }

    @Override
    public Route createRoute(String name, float price, float totalKm, int maxNumberOfUsers, List<Stop> stops) throws ToursException {
        Route route = new Route();
        route.setName(name);
        route.setPrice(price);
        route.setTotalKm(totalKm);
        route.setMaxNumberUsers(maxNumberOfUsers);

        Route persistedRoute = this.routeRepository.save(route);
        persistedRoute.setStops(new ArrayList<>());

        if (stops != null) {
            for (Stop stop : stops) {
                stop.setRoute(persistedRoute);
                this.getSession().merge(stop);
                persistedRoute.getStops().add(stop);
            }
        }

        return persistedRoute;
    }

    @Override
    public Optional<Route> getRouteById(Long id) {
        return this.routeRepository.findById(id);
    }

    @Override
    public List<Route> getRoutesBelowPrice(float price) {
        return this.routeRepository.findAll().stream()
                .filter(route -> route.getPrice() < price)
                .collect(Collectors.toList());
    }

    @Override
    public void assignDriverByUsername(String username, Long idRoute) throws ToursException {
        try {
            User user = this.orThrow(this.getUserByUsername(username), "No pudo realizarse la asignación");
            if (!(user instanceof DriverUser driverUser)) {
                throw new ToursException("No pudo realizarse la asignación");
            }

            Route route = this.orThrow(this.routeRepository.findById(idRoute), "No pudo realizarse la asignación");
            route.addDriver(driverUser);
            this.routeRepository.save(route);
        } catch (RuntimeException e) {
            throw new ToursException("No pudo realizarse la asignación");
        }
    }

    @Override
    public void assignTourGuideByUsername(String username, Long idRoute) throws ToursException {
        try {
            User user = this.orThrow(this.getUserByUsername(username), "No pudo realizarse la asignación");
            if (!(user instanceof TourGuideUser tourGuideUser)) {
                throw new ToursException("No pudo realizarse la asignación");
            }

            Route route = this.orThrow(this.routeRepository.findById(idRoute), "No pudo realizarse la asignación");
            route.addTourGuide(tourGuideUser);
            this.routeRepository.save(route);
        } catch (RuntimeException e) {
            throw new ToursException("No pudo realizarse la asignación");
        }
    }

    @Override
    public Supplier createSupplier(String businessName, String authorizationNumber) throws ToursException {
        if (this.getSupplierByAuthorizationNumber(authorizationNumber).isPresent()) {
            throw new ToursException("Constraint Violation");
        }
        Supplier supplier = new Supplier();
        supplier.setBusinessName(businessName);
        supplier.setAuthorizationNumber(authorizationNumber);
        return this.supplierRepository.save(supplier);
    }

    @Override
    public Service addServiceToSupplier(String name, float price, String description, Supplier supplier) throws ToursException {
        Supplier persistedSupplier = this.orThrow(this.supplierRepository.findById(supplier.getId()), "No existe el proveedor");
        Service service = new Service();
        service.setName(name);
        service.setPrice(price);
        service.setDescription(description);
        service.setSupplier(persistedSupplier);
        persistedSupplier.getServices().add(service);
        Service persistedService = this.serviceRepository.save(service);
        this.supplierRepository.save(persistedSupplier);
        return persistedService;
    }

    @Override
    public Service updateServicePriceById(Long id, float newPrice) throws ToursException {
        Service service = this.orThrow(this.serviceRepository.findById(id), "No existe el producto");
        service.setPrice(newPrice);
        return this.serviceRepository.save(service);
    }

    @Override
    public Optional<Supplier> getSupplierById(Long id) {
        return this.supplierRepository.findById(id);
    }

    @Override
    public Optional<Supplier> getSupplierByAuthorizationNumber(String authorizationNumber) {
        return this.supplierRepository.findAll().stream()
                .filter(supplier -> authorizationNumber.equals(supplier.getAuthorizationNumber()))
                .findFirst();
    }

    @Override
    public Optional<Service> getServiceByNameAndSupplierId(String name, Long id) throws ToursException {
        return this.serviceRepository.findAll().stream()
                .filter(service -> name.equals(service.getName()))
                .filter(service -> service.getSupplier() != null && service.getSupplier().getId() != null && service.getSupplier().getId().equals(id))
                .findFirst();
    }

    @Override
    public Purchase createPurchase(String code, Route route, User user) throws ToursException {
        return this.createPurchase(code, new Date(), route, user);
    }

    @Override
    public Purchase createPurchase(String code, Date date, Route route, User user) throws ToursException {
        if (this.getPurchaseByCode(code).isPresent()) {
            throw new ToursException("Constraint Violation");
        }

        Route persistedRoute = this.orThrow(this.routeRepository.findById(route.getId()), "No puede realizarse la compra");
        User persistedUser = this.orThrow(this.userRepository.findById(user.getId()), "No puede realizarse la compra");

        long purchasesOfRoute = this.purchaseRepository.findAll().stream()
                .filter(purchase -> purchase.getRoute() != null && purchase.getRoute().getId() != null)
                .filter(purchase -> purchase.getRoute().getId().equals(persistedRoute.getId()))
                .count();
        if (purchasesOfRoute >= persistedRoute.getMaxNumberUsers()) {
            throw new ToursException("No puede realizarse la compra");
        }

        Purchase purchase = new Purchase();
        purchase.setCode(code);
        purchase.setDate(date);
        purchase.setRoute(persistedRoute);
        purchase.setUser(persistedUser);
        purchase.setTotalPrice(persistedRoute.getPrice());

        persistedUser.getPurchaseList().add(purchase);
        return this.purchaseRepository.save(purchase);
    }

    @Override
    public ItemService addItemToPurchase(Service service, int quantity, Purchase purchase) throws ToursException {
        Service persistedService = this.orThrow(this.serviceRepository.findById(service.getId()), "No existe el producto");
        Purchase persistedPurchase = this.orThrow(this.purchaseRepository.findById(purchase.getId()), "No existe la compra");

        ItemService item = new ItemService();
        item.setQuantity(quantity);
        item.setService(persistedService);
        item.setPurchase(persistedPurchase);

        persistedPurchase.getItemServiceList().add(item);
        if (persistedService.getItemServiceList() != null) {
            persistedService.getItemServiceList().add(item);
        }
        persistedPurchase.setTotalPrice(persistedPurchase.getTotalPrice() + (persistedService.getPrice() * quantity));

        this.purchaseRepository.save(persistedPurchase);
        return item;
    }

    @Override
    public Optional<Purchase> getPurchaseByCode(String code) {
        return this.purchaseRepository.findAll().stream()
                .filter(purchase -> code.equals(purchase.getCode()))
                .findFirst();
    }

    @Override
    public void deletePurchase(Purchase purchase) throws ToursException {
        Purchase persistedPurchase = purchase.getId() != null
                ? this.purchaseRepository.findById(purchase.getId()).orElse(purchase)
                : this.getPurchaseByCode(purchase.getCode()).orElse(purchase);

        if (persistedPurchase.getUser() != null && persistedPurchase.getUser().getPurchaseList() != null) {
            persistedPurchase.getUser().getPurchaseList().remove(persistedPurchase);
        }

        this.purchaseRepository.delete(persistedPurchase);
    }

    @Override
    public Review addReviewToPurchase(int rating, String comment, Purchase purchase) throws ToursException {
        Purchase persistedPurchase = this.orThrow(this.purchaseRepository.findById(purchase.getId()), "No existe la compra");
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setPurchase(persistedPurchase);
        persistedPurchase.addReview(review);
        Review persistedReview = this.reviewRepository.save(review);
        this.purchaseRepository.save(persistedPurchase);
        return persistedReview;
    }

    @Override
    public void deleteRoute(Route route) throws ToursException {
        Route persistedRoute = this.orThrow(this.routeRepository.findById(route.getId()), "No existe la ruta");
        boolean hasPurchases = this.purchaseRepository.findAll().stream()
                .anyMatch(purchase -> purchase.getRoute() != null && purchase.getRoute().getId() != null && purchase.getRoute().getId().equals(persistedRoute.getId()));
        if (hasPurchases) {
            throw new ToursException("No puede eliminarse una ruta con compras asociadas");
        }
        this.routeRepository.delete(persistedRoute);
    }

    @Override
    public List<Purchase> getAllPurchasesOfUsername(String username) {
        return this.purchaseRepository.findAll().stream()
                .filter(purchase -> purchase.getUser() != null && username.equals(purchase.getUser().getUsername()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUserSpendingMoreThan(float mount) {
        Map<Long, Float> spendingByUser = this.purchaseRepository.findAll().stream()
                .filter(purchase -> purchase.getUser() != null && purchase.getUser().getId() != null)
                .collect(Collectors.groupingBy(purchase -> purchase.getUser().getId(), Collectors.summingDouble(Purchase::getTotalPrice)))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().floatValue()));

        return this.userRepository.findAll().stream()
                .filter(user -> spendingByUser.getOrDefault(user.getId(), 0f) > mount)
                .collect(Collectors.toList());
    }

    @Override
    public List<Supplier> getTopNSuppliersInPurchases(int n) {
        Map<Long, Float> quantityBySupplier = this.purchaseRepository.findAll().stream()
                .flatMap(purchase -> purchase.getItemServiceList().stream())
                .filter(item -> item.getService() != null && item.getService().getSupplier() != null && item.getService().getSupplier().getId() != null)
                .collect(Collectors.groupingBy(item -> item.getService().getSupplier().getId(), Collectors.summingDouble(item -> item.getQuantity())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().floatValue()));

        return this.supplierRepository.findAll().stream()
                .sorted(Comparator.comparingDouble((Supplier supplier) -> quantityBySupplier.getOrDefault(supplier.getId(), 0f)).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public long getCountOfPurchasesBetweenDates(Date start, Date end) {
        return this.purchaseRepository.findAll().stream()
                .filter(purchase -> purchase.getDate() != null)
                .filter(purchase -> !purchase.getDate().before(start) && !purchase.getDate().after(end))
                .count();
    }

    @Override
    public List<Route> getRoutesWithStop(Stop stop) {
        return this.routeRepository.findAll().stream()
                .filter(route -> route.getStops().stream().anyMatch(routeStop -> this.sameStop(routeStop, stop)))
                .collect(Collectors.toList());
    }

    private boolean sameStop(Stop first, Stop second) {
        if (first == null || second == null) {
            return false;
        }
        if (first.getId() != null && second.getId() != null) {
            return first.getId().equals(second.getId());
        }
        return first.getName() != null && first.getName().equals(second.getName());
    }

    @Override
    public Long getMaxStopOfRoutes() {
        return this.routeRepository.findAll().stream()
                .map(route -> (long) route.getStops().size())
                .max(Long::compareTo)
                .orElse(0L);
    }

    @Override
    public List<Route> getRoutsNotSell() {
        List<Long> soldRouteIds = this.purchaseRepository.findAll().stream()
                .map(purchase -> purchase.getRoute())
                .filter(Objects::nonNull)
                .map(Route::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return this.routeRepository.findAll().stream()
                .filter(route -> !soldRouteIds.contains(route.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Route> getTop3RoutesWithMaxRating() {
        Map<Long, Double> ratingByRoute = this.purchaseRepository.findAll().stream()
                .filter(purchase -> purchase.getReview() != null)
                .filter(purchase -> purchase.getRoute() != null && purchase.getRoute().getId() != null)
                .collect(Collectors.groupingBy(purchase -> purchase.getRoute().getId(), Collectors.averagingInt(purchase -> purchase.getReview().getRating())));

        return this.routeRepository.findAll().stream()
                .sorted(Comparator.comparingDouble((Route route) -> ratingByRoute.getOrDefault(route.getId(), 0d)).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    @Override
    public Service getMostDemandedService() {
        Map<Long, Integer> quantityByService = this.purchaseRepository.findAll().stream()
                .flatMap(purchase -> purchase.getItemServiceList().stream())
                .filter(item -> item.getService() != null && item.getService().getId() != null)
                .collect(Collectors.groupingBy(item -> item.getService().getId(), Collectors.summingInt(ItemService::getQuantity)));

        Long serviceId = quantityByService.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return serviceId == null ? null : this.serviceRepository.findById(serviceId).orElse(null);
    }

    @Override
    public List<TourGuideUser> getTourGuidesWithRating1() {
        Set<TourGuideUser> tourGuides = new LinkedHashSet<>();
        this.purchaseRepository.findAll().stream()
                .filter(purchase -> purchase.getReview() != null && purchase.getReview().getRating() == 1)
                .map(Purchase::getRoute)
                .filter(Objects::nonNull)
                .forEach(route -> {
                    if (route.getTourGuideList() != null) {
                        tourGuides.addAll(route.getTourGuideList());
                    }
                });
        return new ArrayList<>(tourGuides);
    }
}
