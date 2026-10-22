package cinema.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends Person {
    private LocalDate customerSince;

    @OneToMany(mappedBy = "customer")
    private List<Purchase> purchases = new ArrayList<>();

    protected Customer() {}

    public Customer(String name, String surname, String phoneNumber, LocalDate dateOfBirth, String email, LocalDate customerSince) {
        super(name, surname, phoneNumber, dateOfBirth, email);
        this.customerSince = customerSince;
    }

    public void addPurchase(Purchase purchase) {
        if (!purchases.contains(purchase)) purchases.add(purchase);
    }

    public LocalDate getCustomerSince() { return customerSince; }
    public List<Purchase> getPurchases() { return purchases; }
}
