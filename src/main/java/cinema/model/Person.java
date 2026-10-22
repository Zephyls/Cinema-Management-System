package cinema.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "person_type")
public abstract class Person {
    @Id @GeneratedValue
    private Integer id;

    private String name;
    private String surname;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String email;

    protected Person() {}

    protected Person(String name, String surname, String phoneNumber, LocalDate dateOfBirth, String email) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getEmail() { return email; }

    @Override
    public String toString() { return name + " " + surname; }
}
