package unlp.info.bd2.model;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.List;

@Entity
public class TourGuideUser extends User {

    private String education;

    @ManyToMany(mappedBy = "tourGuideList")
    private List<Route> routes = new java.util.ArrayList<>();


    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

}
