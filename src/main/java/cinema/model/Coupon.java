package cinema.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Coupon {
    @Id @GeneratedValue
    private Integer id;

    private BigDecimal amount;
    private LocalDate releaseDate;
    private LocalDate expireDate;

    protected Coupon() {}

    public Coupon(BigDecimal amount, LocalDate releaseDate, LocalDate expireDate) {
        this.amount = amount;
        this.releaseDate = releaseDate;
        this.expireDate = expireDate;
    }

    public BigDecimal applyCoupon() { return amount; }
    public boolean isValid() { return expireDate == null || !expireDate.isBefore(LocalDate.now()); }

    @Override
    public String toString() {
        return amount + " PLN discount, valid until " + expireDate;
    }

    public Integer getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public LocalDate getExpireDate() { return expireDate; }
}
