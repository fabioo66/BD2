package unlp.info.bd2.model;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DriverUser extends User {

    @Column
    private String expedient;

    @ManyToMany(mappedBy = "driverList") // lado inverso, Route es el dueño
    private List<Route> routes = new ArrayList<>();

    public String getExpedient() {
        return expedient;
    }

    public void setExpedient(String expedient) {
        this.expedient = expedient;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRouts(List<Route> routs) {
        this.routes = routs;
    }
}
