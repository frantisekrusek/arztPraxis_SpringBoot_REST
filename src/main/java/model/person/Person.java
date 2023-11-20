package model.person;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class Person {
    private String first_name, surname;

    //GETTER, SETTER
    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
