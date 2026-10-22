package cinema.model;

import jakarta.persistence.*;

@Entity
public class Parttime {
    @Id @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Shift shift;

    @OneToOne
    private Employee employee;

    protected Parttime() {}

    public Parttime(Employee employee, Shift shift) {
        this.employee = employee;
        this.shift = shift;
        employee.setParttime(this);
    }

    public Integer getId() { return id; }
    public Shift getShift() { return shift; }
    public Employee getEmployee() { return employee; }
}
