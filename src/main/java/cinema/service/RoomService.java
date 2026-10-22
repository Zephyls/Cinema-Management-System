package cinema.service;

import cinema.model.Room;
import cinema.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class RoomService extends AbstractService {
    public List<Room> getRooms() {
        return findAll(Room.class);
    }

    public Room getRoomById(Integer id) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            Room room = em.find(Room.class, id);
            if (room != null) {
                room.getScreenings().size();
            }
            return room;
        }
    }
}
