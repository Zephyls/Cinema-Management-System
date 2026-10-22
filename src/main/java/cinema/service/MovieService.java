package cinema.service;

import cinema.model.*;
import cinema.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class MovieService extends AbstractService {
    public List<Movie> getMovies() {
        return findAll(Movie.class);
    }

    public Movie getMovieById(Integer id) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            Movie movie = em.find(Movie.class, id);
            if (movie != null) {
                movie.getScreenings().size();
                movie.getGenres().size();
            }
            return movie;
        }
    }

    public Movie createMovie(String type, String title, LocalDate releasedDate, Integer durationMinutes, Integer minimumAge, String genreName) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Movie title cannot be empty");
        if (durationMinutes == null || durationMinutes <= 0) throw new IllegalArgumentException("Duration must be positive");
        try (EntityManager em = JpaUtil.getEntityManager()) {
            em.getTransaction().begin();
            Genre genre = findOrCreateGenre(em, genreName);
            Movie movie;
            if ("ChildrenMovie".equals(type)) {
                ChildrenMovie child = new ChildrenMovie(title.trim(), releasedDate, Duration.ofMinutes(durationMinutes));
                child.setMinimumAge(minimumAge == null ? StaticAttributes.MINIMUM_AGE_CHILDREN : minimumAge);
                child.addSnackAllowed("Popcorn");
                movie = child;
            } else {
                AdultMovie adult = new AdultMovie(title.trim(), releasedDate, Duration.ofMinutes(durationMinutes), minimumAge == null ? 12 : minimumAge);
                movie = adult;
            }
            movie.addGenre(genre);
            em.persist(movie);
            em.getTransaction().commit();
            return movie;
        }
    }

    public void updateMovie(Integer movieId, String title, LocalDate releasedDate, Integer durationMinutes, Integer minimumAge, String genreName) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Movie title cannot be empty");
        if (durationMinutes == null || durationMinutes <= 0) throw new IllegalArgumentException("Duration must be positive");
        try (EntityManager em = JpaUtil.getEntityManager()) {
            em.getTransaction().begin();
            Movie movie = em.find(Movie.class, movieId);
            if (movie == null) throw new IllegalArgumentException("Movie not found");
            movie.setTitle(title.trim());
            movie.setReleasedDate(releasedDate);
            movie.setDuration(Duration.ofMinutes(durationMinutes));
            if (movie instanceof AdultMovie adult) adult.setMinimumAge(minimumAge);
            if (movie instanceof ChildrenMovie child) child.setMinimumAge(minimumAge);
            if (genreName != null && !genreName.isBlank()) {
                movie.getGenres().clear();
                movie.addGenre(findOrCreateGenre(em, genreName));
            }
            em.getTransaction().commit();
        }
    }

    private Genre findOrCreateGenre(EntityManager em, String genreName) {
        String finalName = (genreName == null || genreName.isBlank()) ? "General" : genreName.trim();
        List<Genre> genres = em.createQuery("select g from Genre g where lower(g.name) = lower(:name)", Genre.class)
                .setParameter("name", finalName)
                .getResultList();
        if (!genres.isEmpty()) return genres.get(0);
        Genre genre = new Genre(finalName, finalName + " movies");
        em.persist(genre);
        return genre;
    }
}
