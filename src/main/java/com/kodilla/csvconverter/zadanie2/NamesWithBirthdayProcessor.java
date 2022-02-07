package com.kodilla.csvconverter.zadanie2;

import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class NamesWithBirthdayProcessor implements ItemProcessor<NamesWithBirthday, NamesWithAge> {

    @Override
    public NamesWithAge process(NamesWithBirthday name) throws Exception {
        return new NamesWithAge(name.getName(), name.getSurname(), calculateAge(name.getBirthday()));
    }

    public static int calculateAge(String birthDate) {
        if (birthDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate date = LocalDate.parse(birthDate, formatter);
            return Period.between(date, LocalDate.now()).getYears();
        } else {
            return 0;
        }
    }
}
