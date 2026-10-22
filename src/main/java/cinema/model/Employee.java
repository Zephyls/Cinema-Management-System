package cinema.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("EMPLOYEE")
public abstract class Employee extends Person {
    private BigDecimal baseSalary;
    private LocalDate startDate;

    @ManyToOne
    private Employee supervisor;

    @OneToMany(mappedBy = "supervisor")
    private List<Employee> supervisedEmployees = new ArrayList<>();

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Fulltime fulltime;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Parttime parttime;

    protected Employee() {}

    protected Employee(String name, String surname, String phoneNumber, LocalDate dateOfBirth,
                       String email, BigDecimal baseSalary, LocalDate startDate) {
        super(name, surname, phoneNumber, dateOfBirth, email);
        this.baseSalary = baseSalary;
        this.startDate = startDate;
    }

    public BigDecimal getCurrentSalary() {
        int years = Math.max(0, Period.between(startDate, LocalDate.now()).getYears());
        BigDecimal growth = baseSalary.multiply(StaticAttributes.EMPLOYEE_SALARY_GROWTH_RATE).multiply(BigDecimal.valueOf(years));
        return baseSalary.add(growth);
    }

    public void setSupervisor(Employee supervisor) {
        this.supervisor = supervisor;
        if (supervisor != null && !supervisor.supervisedEmployees.contains(this)) {
            supervisor.supervisedEmployees.add(this);
        }
    }

    public void setFulltime(Fulltime fulltime) {
        this.fulltime = fulltime;
        this.parttime = null;
    }

    public void setParttime(Parttime parttime) {
        this.parttime = parttime;
        this.fulltime = null;
    }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public LocalDate getStartDate() { return startDate; }
    public Employee getSupervisor() { return supervisor; }
    public List<Employee> getSupervisedEmployees() { return supervisedEmployees; }
    public Fulltime getFulltime() { return fulltime; }
    public Parttime getParttime() { return parttime; }
}
