package cinema.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOLBY")
public class Dolby extends Room {
    private Boolean vibratingSeats;

    protected Dolby() {}

    public Dolby(Integer roomNumber, Integer numberOfSeats, Integer seatPerRow, Boolean vibratingSeats) {
        super(roomNumber, numberOfSeats, seatPerRow);
        this.vibratingSeats = vibratingSeats;
    }

    public Boolean getVibratingSeats() { return vibratingSeats; }
}
