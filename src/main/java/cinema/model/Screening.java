package cinema.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Screening {
    @Id @GeneratedValue
    private Integer id;

    private LocalDateTime startDateTime;
    private BigDecimal defaultTicketPrice;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Movie movie;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Room room;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    protected Screening() {}

    public Screening(Movie movie, Room room, LocalDateTime startDateTime, BigDecimal defaultTicketPrice) {
        if (defaultTicketPrice.compareTo(BigDecimal.valueOf(StaticAttributes.MINIMUM_SCREENING_PRICE)) < 0) {
            throw new IllegalArgumentException("Ticket price cannot be lower than minimum price");
        }
        this.movie = movie;
        this.room = room;
        this.startDateTime = startDateTime;
        this.defaultTicketPrice = defaultTicketPrice;
    }

    public LocalDateTime getEndDateTime() {
        return startDateTime.plus(movie.getDuration()).plusMinutes(30);
    }

    public void updateScreening(LocalDateTime newStartDateTime, BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.valueOf(StaticAttributes.MINIMUM_SCREENING_PRICE)) < 0) {
            throw new IllegalArgumentException("Ticket price cannot be lower than minimum price");
        }
        this.startDateTime = newStartDateTime;
        this.defaultTicketPrice = newPrice;
    }

    public void addTicket(Ticket ticket) {
        if (!tickets.contains(ticket)) {
            tickets.add(ticket);
            ticket.setScreening(this);
        }
    }

    public Integer getId() { return id; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public BigDecimal getDefaultTicketPrice() { return defaultTicketPrice; }
    public Movie getMovie() { return movie; }
    public Room getRoom() { return room; }
    public List<Ticket> getTickets() { return tickets; }

    public void setMovie(Movie movie) { this.movie = movie; }
    public void setRoom(Room room) { this.room = room; }

    @Override
    public String toString() {
        return movie.getTitle() + " | Room " + room.getRoomNumber() + " | " + startDateTime;
    }
}
