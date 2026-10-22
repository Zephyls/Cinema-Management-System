package cinema.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ADULT")
public class AdultMovie extends Movie {
    private Integer minimumAge;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> alcoholsAllowed = new ArrayList<>();

    protected AdultMovie() {}

    public AdultMovie(String title, LocalDate releasedDate, Duration duration, Integer minimumAge) {
        super(title, releasedDate, duration);
        this.minimumAge = minimumAge;
    }

    public void addAlcoholAllowed(String alcohol) { alcoholsAllowed.add(alcohol); }
    public Integer getMinimumAge() { return minimumAge; }
    public void setMinimumAge(Integer minimumAge) { this.minimumAge = minimumAge; }
    public List<String> getAlcoholsAllowed() { return alcoholsAllowed; }
}
