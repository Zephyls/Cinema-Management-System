package cinema.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Genre {
    @Id @GeneratedValue
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "genres")
    private List<Movie> movies = new ArrayList<>();

    protected Genre() {}

    public Genre(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Movie> getMovies() { return movies; }

    @Override
    public String toString() { return name; }
}
