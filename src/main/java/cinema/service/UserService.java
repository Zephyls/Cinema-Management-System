package cinema.service;

import cinema.model.*;
import cinema.util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class UserService extends AbstractService {
    public List<Customer> getCustomers() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            List<Customer> customers = em.createQuery("select c from Customer c order by c.surname, c.name", Customer.class).getResultList();
            customers.forEach(c -> c.getPurchases().size());
            return customers;
        }
    }

    public List<Employee> getEmployees() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            List<Employee> employees = em.createQuery("select e from Employee e order by e.surname, e.name", Employee.class).getResultList();
            for (Employee e : employees) {
                if (e.getFulltime() != null) e.getFulltime().getWeeklyHours();
                if (e.getParttime() != null) e.getParttime().getShift();
                e.getSupervisedEmployees().size();
            }
            return employees;
        }
    }

    public List<Person> getAllUsers() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            List<Person> users = em.createQuery("select p from Person p order by p.surname, p.name", Person.class).getResultList();
            for (Person person : users) {
                if (person instanceof Customer customer) {
                    customer.getPurchases().size();
                }
                if (person instanceof Employee employee) {
                    if (employee.getFulltime() != null) employee.getFulltime().getWeeklyHours();
                    if (employee.getParttime() != null) employee.getParttime().getShift();
                    employee.getSupervisedEmployees().size();
                }
            }
            return users;
        }
    }

    public Customer getCustomerById(Integer id) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            Customer customer = em.find(Customer.class, id);
            if (customer != null) {
                customer.getPurchases().size();
                for (Purchase p : customer.getPurchases()) {
                    p.getTickets().size();
                    p.getCoupons().size();
                    for (Ticket t : p.getTickets()) {
                        t.getScreening().getMovie().getTitle();
                        t.getScreening().getRoom().getRoomNumber();
                    }
                }
            }
            return customer;
        }
    }

    public Employee getEmployeeById(Integer id) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            Employee employee = em.find(Employee.class, id);
            if (employee != null) {
                if (employee.getFulltime() != null) employee.getFulltime().getWeeklyHours();
                if (employee.getParttime() != null) employee.getParttime().getShift();
                employee.getSupervisedEmployees().size();
                if (employee.getSupervisor() != null) employee.getSupervisor().getName();
            }
            return employee;
        }
    }
}
