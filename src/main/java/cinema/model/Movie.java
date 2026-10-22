package cinema.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "movie_type")
public abstract class Movie {
    @Id @GeneratedValue
    private Integer id;

    private String title;
    private LocalDate releasedDate;
    private Duration duration;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Genre> genres = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startDateTime")
    private List<Screening> screenings = new ArrayList<>();

    protected Movie() {}

    protected Movie(String title, LocalDate releasedDate, Duration duration) {
        this.title = title;
        this.releasedDate = releasedDate;
        this.duration = duration;
    }

    public void addGenre(Genre genre) {
        if (!genres.contains(genre)) {
            genres.add(genre);
            genre.getMovies().add(this);
        }
    }

    public void addScreening(Screening screening) {
        if (!screenings.contains(screening)) {
            screenings.add(screening);
            screening.setMovie(this);
        }
    }

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public LocalDate getReleasedDate() { return releasedDate; }
    public Duration getDuration() { return duration; }
    public void setTitle(String title) { this.title = title; }
    public void setReleasedDate(LocalDate releasedDate) { this.releasedDate = releasedDate; }
    public void setDuration(Duration duration) { this.duration = duration; }
    public List<Genre> getGenres() { return genres; }
    public List<Screening> getScreenings() { return screenings; }

    public String getMovieType() { return getClass().getSimpleName(); }

    @Override
    public String toString() { return title; }
}
