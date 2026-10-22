package cinema.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("IMAX")
public class IMAX extends Room {
    private double screenHeight;
    private double screenWidth;

    protected IMAX() {}

    public IMAX(Integer roomNumber, Integer numberOfSeats, Integer seatPerRow, double screenHeight, double screenWidth) {
        super(roomNumber, numberOfSeats, seatPerRow);
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

    public double getScreenHeight() { return screenHeight; }
    public double getScreenWidth() { return screenWidth; }
}
