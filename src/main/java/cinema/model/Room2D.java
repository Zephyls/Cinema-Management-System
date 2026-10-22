package cinema.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("2D")
public class Room2D extends Room {
    private String projectionType;

    protected Room2D() {}

    public Room2D(Integer roomNumber, Integer numberOfSeats, Integer seatPerRow, String projectionType) {
        super(roomNumber, numberOfSeats, seatPerRow);
        this.projectionType = projectionType;
    }

    public String getProjectionType() { return projectionType; }

    @Override
    public String getRoomType() { return "2D"; }
}
