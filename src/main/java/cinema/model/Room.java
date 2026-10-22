package cinema.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "room_type")
public abstract class Room {
    @Id @GeneratedValue
    private Integer id;

    @Column(unique = true, nullable = false)
    private Integer roomNumber;

    private Integer numberOfSeats;
    private Integer seatPerRow;
    private Integer maintenancePeriod = StaticAttributes.MAINTENANCE_PERIOD_DAYS;

    @OneToMany(mappedBy = "room")
    @OrderBy("startDateTime")
    private List<Screening> screenings = new ArrayList<>();

    protected Room() {}

    protected Room(Integer roomNumber, Integer numberOfSeats, Integer seatPerRow) {
        this.roomNumber = roomNumber;
        this.numberOfSeats = numberOfSeats;
        this.seatPerRow = seatPerRow;
    }

    public void addScreening(Screening screening) {
        if (!screenings.contains(screening)) {
            screenings.add(screening);
            screening.setRoom(this);
        }
    }

    public Integer getId() { return id; }
    public Integer getRoomNumber() { return roomNumber; }
    public Integer getNumberOfSeats() { return numberOfSeats; }
    public Integer getSeatPerRow() { return seatPerRow; }
    public Integer getMaintenancePeriod() { return maintenancePeriod; }
    public List<Screening> getScreenings() { return screenings; }
    public String getRoomType() { return getClass().getSimpleName(); }

    @Override
    public String toString() { return "Room " + roomNumber + " (" + getRoomType() + ")"; }
}
