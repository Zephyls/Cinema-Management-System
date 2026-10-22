package cinema.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchases")
public class Purchase {
    @Id @GeneratedValue
    private Integer id;

    @Column(unique = true, nullable = false)
    private String purchaseNumber;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime lastModification;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    @ManyToMany
    private List<Coupon> coupons = new ArrayList<>();

    protected Purchase() {}

    public Purchase(String purchaseNumber, Customer customer, PaymentType paymentType) {
        this.purchaseNumber = purchaseNumber;
        this.customer = customer;
        this.paymentType = paymentType;
        this.paymentStatus = PaymentStatus.PENDING;
        this.lastModification = LocalDateTime.now();
    }

    public void addTicket(Ticket ticket) {
        if (!tickets.contains(ticket)) {
            tickets.add(ticket);
            ticket.setPurchase(this);
        }
    }

    public void addCoupon(Coupon coupon) {
        if (!coupons.contains(coupon)) coupons.add(coupon);
    }

    public BigDecimal getTotalPrice() {
        BigDecimal ticketsSum = tickets.stream().map(t -> t.getScreening().getDefaultTicketPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal couponSum = coupons.stream().map(Coupon::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal result = ticketsSum.subtract(couponSum);
        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
    }

    public String getPurchaseByNumber() { return purchaseNumber; }

    public void updatePurchaseStatus(PaymentStatus newStatus) {
        this.paymentStatus = newStatus;
        this.lastModification = LocalDateTime.now();
    }

    public void finalizePurchase() { updatePurchaseStatus(PaymentStatus.CONFIRMED); }
    public void cancelPurchase() { updatePurchaseStatus(PaymentStatus.CANCELLED); }
    public void archivePurchase() { updatePurchaseStatus(PaymentStatus.ARCHIVED); }

    @Override
    public String toString() {
        return purchaseNumber + " | " + customer + " | " + paymentStatus + " | " + getTotalPrice() + " PLN";
    }

    public Integer getId() { return id; }
    public String getPurchaseNumber() { return purchaseNumber; }
    public PaymentType getPaymentType() { return paymentType; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public LocalDateTime getLastModification() { return lastModification; }
    public Customer getCustomer() { return customer; }
    public List<Ticket> getTickets() { return tickets; }
    public List<Coupon> getCoupons() { return coupons; }
}
