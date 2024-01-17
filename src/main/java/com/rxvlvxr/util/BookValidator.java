package com.rxvlvxr.util;

import com.rxvlvxr.models.Book;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.Year;

@Component
public class BookValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Book book = (Book) target;

        int currentYear = Year.now().getValue();

        if (book.getYear() > currentYear)
            errors.rejectValue
                    (
                            "year",
                            "",
                            "Превышено максимальное допустимое значения года издания: " + currentYear
                    );
    }
}
