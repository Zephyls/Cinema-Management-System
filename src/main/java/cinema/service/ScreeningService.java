package cinema.service;

import cinema.model.Movie;
import cinema.model.Room;
import cinema.model.Screening;
import cinema.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ScreeningService extends AbstractService {
    public List<Screening> getScreenings() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("select s from Screening s order by s.startDateTime", Screening.class).getResultList();
        }
    }

    public Screening getScreeningById(Integer id) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            Screening screening = em.find(Screening.class, id);
            if (screening != null) {
                screening.getMovie().getTitle();
                screening.getRoom().getRoomNumber();
            }
            return screening;
        }
    }

    public List<Screening> getScreeningsInRoomByDate(Room room, LocalDate date) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery(
                    "select s from Screening s where s.room.id = :roomId and s.startDateTime between :from and :to order by s.startDateTime",
                    Screening.class)
                    .setParameter("roomId", room.getId())
                    .setParameter("from", date.atStartOfDay())
                    .setParameter("to", date.plusDays(1).atStartOfDay().minusSeconds(1))
                    .getResultList();
        }
    }

    public boolean validateStartingDateTime(Room room, Movie movie, LocalDateTime startDateTime) {
        LocalDateTime endDateTime = startDateTime.plus(movie.getDuration()).plusMinutes(30);
        return !hasCollision(room, startDateTime, endDateTime);
    }

    public boolean validateEndingDateTime(Room room, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return !hasCollision(room, startDateTime, endDateTime);
    }

    public Screening createScreening(Integer movieId, Integer roomId, LocalDateTime startDateTime, BigDecimal defaultTicketPrice) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            em.getTransaction().begin();
            Movie movie = em.find(Movie.class, movieId);
            Room room = em.find(Room.class, roomId);

            if (movie == null || room == null) {
                throw new IllegalArgumentException("Movie or room was not found");
            }
            if (startDateTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Start time cannot be in the past");
            }

            LocalDateTime endDateTime = startDateTime.plus(movie.getDuration()).plusMinutes(30);
            if (!validateStartingDateTime(room, movie, startDateTime) || !validateEndingDateTime(room, startDateTime, endDateTime)) {
                throw new IllegalArgumentException("Selected period overlaps with an existing screening. New screening would end at " + endDateTime.toLocalTime());
            }

            Screening screening = new Screening(movie, room, startDateTime, defaultTicketPrice);
            em.persist(screening);
            em.getTransaction().commit();
            return screening;
        }
    }

    private boolean hasCollision(Room room, LocalDateTime newStart, LocalDateTime newEnd) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            List<Screening> screenings = em.createQuery(
                    "select s from Screening s where s.room.id = :roomId order by s.startDateTime",
                    Screening.class)
                    .setParameter("roomId", room.getId())
                    .getResultList();

            for (Screening existing : screenings) {
                LocalDateTime existingStart = existing.getStartDateTime();
                LocalDateTime existingEnd = existing.getEndDateTime();
                if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                    return true;
                }
            }
            return false;
        }
    }
}
