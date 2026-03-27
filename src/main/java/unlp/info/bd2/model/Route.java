package unlp.info.bd2.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private float totalKm;

    @Column(nullable = false)
    private int maxNumberUsers;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stop> stops = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "route_driver", // nombre de la tabla join
            joinColumns = @JoinColumn(name = "route_id"), // FK hacia Route
            inverseJoinColumns = @JoinColumn(name = "driver_id") // FK hacia DriverUser
    )
    private List<DriverUser> driverList = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "route_tourguide", // nombre de la tabla join
            joinColumns = @JoinColumn(name = "route_id"), // FK hacia Route
            inverseJoinColumns = @JoinColumn(name = "tourguide_id") // FK hacia TourGuideUser
    )
    private List<TourGuideUser> tourGuideList = new ArrayList<>();

    public void addDriver(DriverUser driver) {
        driverList.add(driver);
        driver.getRoutes().add(this); // sincronizamos el otro lado
    }

    public void addTourGuide(TourGuideUser guide) {
        tourGuideList.add(guide);
        guide.getRoutes().add(this); // sincronizamos el otro lado
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(float totalKm) {
        this.totalKm = totalKm;
    }

    public int getMaxNumberUsers() {
        return maxNumberUsers;
    }

    public void setMaxNumberUsers(int maxNumberUsers) {
        this.maxNumberUsers = maxNumberUsers;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public List<DriverUser> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<DriverUser> driverList) {
        this.driverList = driverList;
    }

    public List<TourGuideUser> getTourGuideList() {
        return tourGuideList;
    }

    public void setTourGuideList(List<TourGuideUser> tourGuideList) {
        this.tourGuideList = tourGuideList;
    }

}
