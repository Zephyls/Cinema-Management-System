package cinema.service;

import cinema.model.*;
import cinema.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PurchaseService extends AbstractService {
    public List<Purchase> getPurchases() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            List<Purchase> purchases = em.createQuery("select p from Purchase p order by p.lastModification desc", Purchase.class).getResultList();
            initializePurchases(purchases);
            return purchases;
        }
    }

    public List<Purchase> getPurchasesForCustomer(Integer customerId) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            List<Purchase> purchases = em.createQuery(
                    "select p from Purchase p where p.customer.id = :customerId order by p.lastModification desc", Purchase.class)
                    .setParameter("customerId", customerId)
                    .getResultList();
            initializePurchases(purchases);
            return purchases;
        }
    }

    public List<Coupon> getAvailableCoupons() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("select c from Coupon c where c.expireDate >= :today order by c.amount desc", Coupon.class)
                    .setParameter("today", LocalDate.now())
                    .getResultList();
        }
    }

    public Purchase createPurchase(Customer customer, PaymentType paymentType) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            em.getTransaction().begin();
            Customer managedCustomer = em.merge(customer);
            Purchase purchase = new Purchase("P-" + UUID.randomUUID().toString().substring(0, 8), managedCustomer, paymentType);
            em.persist(purchase);
            em.getTransaction().commit();
            return purchase;
        }
    }

    public Purchase createPurchaseForScreening(Integer customerId, Integer screeningId, Integer seatNumber, PaymentType paymentType) {
        return createPurchaseForScreening(customerId, screeningId, seatNumber, paymentType, null);
    }

    public Purchase createPurchaseForScreening(Integer customerId, Integer screeningId, Integer seatNumber, PaymentType paymentType, Integer couponId) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            em.getTransaction().begin();
            Customer customer = em.find(Customer.class, customerId);
            Screening screening = em.find(Screening.class, screeningId);
            if (customer == null) throw new IllegalArgumentException("Customer not found");
            if (screening == null) throw new IllegalArgumentException("Screening not found");
            if (seatNumber == null || seatNumber < 1 || seatNumber > screening.getRoom().getNumberOfSeats()) {
                throw new IllegalArgumentException("Seat number is outside room capacity");
            }
            boolean occupied = em.createQuery(
                            "select count(t) from Ticket t where t.screening.id = :screeningId and t.seatNumber = :seatNumber " +
                                    "and t.purchase.paymentStatus in (:activeStatuses)", Long.class)
                    .setParameter("screeningId", screeningId)
                    .setParameter("seatNumber", seatNumber)
                    .setParameter("activeStatuses", List.of(PaymentStatus.PENDING, PaymentStatus.CONFIRMED))
                    .getSingleResult() > 0;
            if (occupied) throw new IllegalArgumentException("Selected seat is already occupied");

            Purchase purchase = new Purchase("P-" + UUID.randomUUID().toString().substring(0, 8), customer, paymentType);
            Ticket ticket = new Ticket(screening, seatNumber);
            purchase.addTicket(ticket);
            if (couponId != null) {
                Coupon coupon = em.find(Coupon.class, couponId);
                if (coupon != null && coupon.isValid()) purchase.addCoupon(coupon);
            }
            customer.addPurchase(purchase);
            screening.addTicket(ticket);
            em.persist(ticket);
            em.persist(purchase);
            em.getTransaction().commit();

            purchase.getTickets().size();
            purchase.getCoupons().size();
            return purchase;
        }
    }

    public void finalizePurchase(Integer purchaseId) {
        updatePurchaseStatus(purchaseId, PaymentStatus.CONFIRMED);
    }

    public void cancelPurchase(Integer purchaseId) {
        updatePurchaseStatus(purchaseId, PaymentStatus.CANCELLED);
    }

    public void archivePurchase(Integer purchaseId) {
        updatePurchaseStatus(purchaseId, PaymentStatus.ARCHIVED);
    }

    public void deletePurchase(Integer purchaseId) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            em.getTransaction().begin();
            Purchase purchase = em.find(Purchase.class, purchaseId);
            if (purchase != null && purchase.getPaymentStatus() == PaymentStatus.ARCHIVED) {
                em.remove(purchase);
            }
            em.getTransaction().commit();
        }
    }

    public int cancelExpiredPendingPurchases() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            em.getTransaction().begin();
            List<Purchase> expired = em.createQuery(
                    "select p from Purchase p where p.paymentStatus = :status and p.lastModification < :limit", Purchase.class)
                    .setParameter("status", PaymentStatus.PENDING)
                    .setParameter("limit", LocalDateTime.now().minusMinutes(10))
                    .getResultList();
            for (Purchase purchase : expired) {
                purchase.cancelPurchase();
            }
            em.getTransaction().commit();
            return expired.size();
        }
    }

    private void updatePurchaseStatus(Integer purchaseId, PaymentStatus status) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            em.getTransaction().begin();
            Purchase purchase = em.find(Purchase.class, purchaseId);
            if (purchase != null) {
                purchase.updatePurchaseStatus(status);
            }
            em.getTransaction().commit();
        }
    }

    private void initializePurchases(List<Purchase> purchases) {
        for (Purchase purchase : purchases) {
            purchase.getCustomer().getName();
            purchase.getTickets().size();
            purchase.getCoupons().size();
            for (Ticket ticket : purchase.getTickets()) {
                ticket.getScreening().getMovie().getTitle();
                ticket.getScreening().getRoom().getRoomNumber();
            }
        }
    }
}
