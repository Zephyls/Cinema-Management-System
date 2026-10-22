package cinema.model;

import jakarta.persistence.*;

@Entity
public class Fulltime {
    @Id @GeneratedValue
    private Integer id;

    private Integer weeklyHours;

    @OneToOne
    private Employee employee;

    protected Fulltime() {}

    public Fulltime(Employee employee, Integer weeklyHours) {
        this.employee = employee;
        this.weeklyHours = weeklyHours;
        employee.setFulltime(this);
    }

    public Integer getId() { return id; }
    public Integer getWeeklyHours() { return weeklyHours; }
    public Employee getEmployee() { return employee; }
}
