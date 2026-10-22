package cinema.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("SCREENING_EMPLOYEE")
public class ScreeningEmployee extends Employee {
    private Boolean equipmentTrainingCompleted;

    protected ScreeningEmployee() {}

    public ScreeningEmployee(String name, String surname, String phoneNumber, LocalDate dateOfBirth, String email, BigDecimal baseSalary, LocalDate startDate, Boolean equipmentTrainingCompleted) {
        super(name, surname, phoneNumber, dateOfBirth, email, baseSalary, startDate);
        this.equipmentTrainingCompleted = equipmentTrainingCompleted;
    }

    public Boolean getEquipmentTrainingCompleted() { return equipmentTrainingCompleted; }
}
