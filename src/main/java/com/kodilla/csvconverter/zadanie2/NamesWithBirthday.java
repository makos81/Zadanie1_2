package com.kodilla.csvconverter.zadanie2;

import java.time.LocalDate;

public class NamesWithBirthday {
    private String name;
    private String surname;
    private String birthday;

    public NamesWithBirthday(String name, String surname, String birthday) {
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
    }

    public NamesWithBirthday(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
