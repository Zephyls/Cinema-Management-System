package cinema.service;

import cinema.model.*;
import cinema.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DataSeeder {
    public void seed() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            Long count = em.createQuery("select count(m) from Movie m", Long.class).getSingleResult();
            if (count > 0) return;

            em.getTransaction().begin();

            Genre sciFi = new Genre("Sci-Fi", "Science fiction movies");
            Genre animation = new Genre("Animation", "Movies for children and families");
            Genre thriller = new Genre("Thriller", "Suspense movies");
            Genre action = new Genre("Action", "Action and superhero movies");
            Genre adventure = new Genre("Adventure", "Adventure movies");
            em.persist(sciFi);
            em.persist(animation);
            em.persist(thriller);
            em.persist(action);
            em.persist(adventure);

            AdultMovie dune = new AdultMovie("Dune: Part Two", LocalDate.of(2024, 3, 1), Duration.ofMinutes(166), 13);
            dune.addAlcoholAllowed("Beer");
            dune.addGenre(sciFi);
            dune.addGenre(adventure);

            AdultMovie oppenheimer = new AdultMovie("Oppenheimer", LocalDate.of(2023, 7, 21), Duration.ofMinutes(180), 15);
            oppenheimer.addGenre(thriller);

            AdultMovie avengers = new AdultMovie("Avengers: Endgame", LocalDate.of(2019, 4, 26), Duration.ofMinutes(181), 13);
            avengers.addAlcoholAllowed("Beer");
            avengers.addGenre(action);
            avengers.addGenre(sciFi);

            AdultMovie spiderMan = new AdultMovie("Spider-Man: No Way Home", LocalDate.of(2021, 12, 17), Duration.ofMinutes(148), 12);
            spiderMan.addGenre(action);
            spiderMan.addGenre(adventure);

            AdultMovie blackPanther = new AdultMovie("Black Panther", LocalDate.of(2018, 2, 16), Duration.ofMinutes(134), 12);
            blackPanther.addGenre(action);
            blackPanther.addGenre(adventure);

            ChildrenMovie insideOut = new ChildrenMovie("Inside Out 2", LocalDate.of(2024, 6, 14), Duration.ofMinutes(96));
            insideOut.addSnackAllowed("Popcorn");
            insideOut.addSnackAllowed("Juice");
            insideOut.addGenre(animation);

            ChildrenMovie coco = new ChildrenMovie("Coco", LocalDate.of(2017, 11, 22), Duration.ofMinutes(105));
            coco.addSnackAllowed("Popcorn");
            coco.addSnackAllowed("Water");
            coco.addGenre(animation);
            coco.addGenre(adventure);

            em.persist(dune);
            em.persist(oppenheimer);
            em.persist(avengers);
            em.persist(spiderMan);
            em.persist(blackPanther);
            em.persist(insideOut);
            em.persist(coco);

            Room roomA1 = new Room2D(1, 72, 12, "2D Digital");
            Room roomA2 = new Room2D(2, 64, 8, "Laser 4K");
            Room roomB1 = new IMAX(3, 90, 10, 16.0, 24.0);
            Room roomC2 = new Dolby(4, 48, 6, true);
            em.persist(roomA1);
            em.persist(roomA2);
            em.persist(roomB1);
            em.persist(roomC2);

            Screening s1 = new Screening(dune, roomA2, LocalDateTime.now().plusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(0), new BigDecimal("45"));
            Screening s2 = new Screening(dune, roomA1, LocalDateTime.now().plusDays(2).withHour(20).withMinute(30).withSecond(0).withNano(0), new BigDecimal("39"));
            Screening s3 = new Screening(insideOut, roomC2, LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0), new BigDecimal("35"));
            Screening s4 = new Screening(avengers, roomB1, LocalDateTime.now().plusDays(3).withHour(17).withMinute(45).withSecond(0).withNano(0), new BigDecimal("42"));
            Screening s5 = new Screening(spiderMan, roomA2, LocalDateTime.now().plusDays(4).withHour(14).withMinute(15).withSecond(0).withNano(0), new BigDecimal("37"));
            Screening s6 = new Screening(blackPanther, roomA1, LocalDateTime.now().plusDays(5).withHour(19).withMinute(0).withSecond(0).withNano(0), new BigDecimal("40"));
            em.persist(s1);
            em.persist(s2);
            em.persist(s3);
            em.persist(s4);
            em.persist(s5);
            em.persist(s6);

            Customer tony = new Customer("Tony", "Stark", "500100200", LocalDate.of(1970, 5, 29), "tony.stark@example.com", LocalDate.now().minusYears(2));
            Customer steve = new Customer("Steve", "Rogers", "500700800", LocalDate.of(1918, 7, 4), "steve.rogers@example.com", LocalDate.now().minusMonths(8));

            Cashier natasha = new Cashier("Natasha", "Romanoff", "500300400", LocalDate.of(1984, 11, 22), "natasha@cinema.pl", new BigDecimal("4200"), LocalDate.now().minusYears(3));
            natasha.addLanguage("Polish");
            natasha.addLanguage("English");

            ScreeningEmployee peter = new ScreeningEmployee("Peter", "Parker", "500500500", LocalDate.of(2001, 8, 10), "peter@cinema.pl", new BigDecimal("4500"), LocalDate.now().minusYears(2), true);
            Employee bruce = new Cashier("Bruce", "Banner", "500900900", LocalDate.of(1969, 12, 18), "bruce@cinema.pl", new BigDecimal("5200"), LocalDate.now().minusYears(4));

            new Fulltime(peter, 40);
            new Parttime(natasha, Shift.NIGHT);
            new Fulltime(bruce, 40);

            em.persist(tony);
            em.persist(steve);
            em.persist(peter);
            em.persist(natasha);
            em.persist(bruce);

            Coupon coupon = new Coupon(new BigDecimal("10"), LocalDate.now().minusDays(5), LocalDate.now().plusDays(30));
            em.persist(coupon);

            Purchase purchase1 = new Purchase("P-1001", tony, PaymentType.BLIK);
            Ticket ticket1 = new Ticket(s1, 12);
            purchase1.addTicket(ticket1);
            purchase1.addCoupon(coupon);
            purchase1.finalizePurchase();
            tony.addPurchase(purchase1);

            Purchase purchase2 = new Purchase("P-1002", steve, PaymentType.CREDIT_CARD);
            Ticket ticket2 = new Ticket(s3, 6);
            purchase2.addTicket(ticket2);
            steve.addPurchase(purchase2);

            em.persist(ticket1);
            em.persist(ticket2);
            em.persist(purchase1);
            em.persist(purchase2);

            em.getTransaction().commit();
        }
    }
}
