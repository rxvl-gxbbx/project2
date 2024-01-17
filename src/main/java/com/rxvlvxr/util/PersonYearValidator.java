package com.rxvlvxr.util;


import com.rxvlvxr.models.Person;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class PersonYearValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        final int minAge = 14;
        int localYear = LocalDate.now(ZoneId.of("UTC+3")).getYear();

        if (localYear < person.getBirthYear() + minAge) {
            errors.rejectValue
                    (
                            "birthYear",
                            "",
                            "Превышено максимальное допустимое значения года рождения: " + (localYear - minAge)
                    );
        }
    }
}
