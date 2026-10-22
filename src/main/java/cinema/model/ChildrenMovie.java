package cinema.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CHILDREN")
public class ChildrenMovie extends Movie {
    private Integer minimumAge = StaticAttributes.MINIMUM_AGE_CHILDREN;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> snacksAllowed = new ArrayList<>();

    protected ChildrenMovie() {}

    public ChildrenMovie(String title, LocalDate releasedDate, Duration duration) {
        super(title, releasedDate, duration);
    }

    public void addSnackAllowed(String snack) { snacksAllowed.add(snack); }
    public Integer getMinimumAge() { return minimumAge; }
    public void setMinimumAge(Integer minimumAge) { this.minimumAge = minimumAge; }
    public List<String> getSnacksAllowed() { return snacksAllowed; }
}
