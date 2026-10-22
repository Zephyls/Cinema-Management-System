package cinema.model;

import jakarta.persistence.*;

@Entity
public class Ticket {
    @Id @GeneratedValue
    private Integer id;

    private Integer seatNumber;

    @ManyToOne(optional = false)
    private Screening screening;

    @ManyToOne
    private Purchase purchase;

    protected Ticket() {}

    public Ticket(Screening screening, Integer seatNumber) {
        if (seatNumber < 1 || seatNumber > screening.getRoom().getNumberOfSeats()) {
            throw new IllegalArgumentException("Seat number is outside room capacity");
        }
        this.screening = screening;
        this.seatNumber = seatNumber;
    }

    public boolean checkOccupiedSeat() {
        return screening.getTickets().stream().anyMatch(t -> t != this && t.getSeatNumber().equals(seatNumber));
    }

    public Integer getId() { return id; }
    public Integer getSeatNumber() { return seatNumber; }
    public Screening getScreening() { return screening; }
    public Purchase getPurchase() { return purchase; }

    public void setScreening(Screening screening) { this.screening = screening; }
    public void setPurchase(Purchase purchase) { this.purchase = purchase; }

    @Override
    public String toString() { return "Seat " + seatNumber + " for " + screening; }
}
