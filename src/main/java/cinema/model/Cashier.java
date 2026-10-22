package cinema.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CASHIER")
public class Cashier extends Employee {
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> languagesSpoken = new ArrayList<>();

    protected Cashier() {}

    public Cashier(String name, String surname, String phoneNumber, LocalDate dateOfBirth, String email, BigDecimal baseSalary, LocalDate startDate) {
        super(name, surname, phoneNumber, dateOfBirth, email, baseSalary, startDate);
    }

    public void addLanguage(String language) { languagesSpoken.add(language); }
    public List<String> getLanguagesSpoken() { return languagesSpoken; }
}
