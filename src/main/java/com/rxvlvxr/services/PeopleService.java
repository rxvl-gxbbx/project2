package com.rxvlvxr.services;

import com.rxvlvxr.models.Book;
import com.rxvlvxr.models.Person;
import com.rxvlvxr.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Optional<Person> findById(int id) {
        return peopleRepository.findById(id);
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);

        Optional<Person> optionalPerson = peopleRepository.findById(id);

        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            List<Book> books = person.getBooks();

            updatedPerson.setBooks(books);
            person.getBooks().clear();

            for (Book book : books) {
                book.setPerson(updatedPerson);
            }
        }

        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

    public List<Book> findAllBooksByPersonId(int id) {
        Optional<Person> optionalPerson = peopleRepository.findById(id);
        List<Book> books = Collections.emptyList();

        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            Hibernate.initialize(person.getBooks());
            books = person.getBooks();
        }

        return books;
    }

    public Optional<Person> findPersonByName(String name) {
        return Optional.ofNullable(peopleRepository.findPersonByName(name));
    }
}
