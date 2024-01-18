package com.rxvlvxr.services;

import com.rxvlvxr.models.Book;
import com.rxvlvxr.models.Person;
import com.rxvlvxr.repositories.BooksRepository;
import com.rxvlvxr.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {
    private final BooksRepository booksRepository;
    private final PeopleRepository peopleRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository, PeopleRepository peopleRepository) {
        this.booksRepository = booksRepository;
        this.peopleRepository = peopleRepository;
    }


    public List<Book> findAll(Optional<Integer> page, Optional<Integer> booksPerPage, boolean sortByYear) {
        Sort sortCriteria = sortByYear ? Sort.by("year") : Sort.unsorted();
        int itemsPerPage = booksPerPage.orElse(Integer.MAX_VALUE);
        int pageCount = page.orElse(0);

        return booksRepository.findAll(PageRequest.of(pageCount, itemsPerPage, sortCriteria)).getContent();
    }

    public Optional<Book> findById(int id) {
        return booksRepository.findById(id);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        Optional<Book> optionalBook = booksRepository.findById(id);

        optionalBook.ifPresent(book -> {
            Person person = book.getPerson();

            if (person != null) {
                book.setPerson(null);
                person.getBooks().remove(book);
                updatedBook.setPerson(person);
                updatedBook.setTakenAt(book.getTakenAt());
            }
        });

        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    public Optional<Person> findPersonByBookId(int id) {
        final Person[] person = {null};
        Optional<Book> optionalBook = booksRepository.findById(id);

        optionalBook.ifPresent(book -> {
            Hibernate.initialize(book.getPerson());
            person[0] = book.getPerson();
        });

        return Optional.ofNullable(person[0]);
    }

    @Transactional
    public void assignToPerson(int bookId, int personId) {
        Optional<Book> optionalBook = booksRepository.findById(bookId);
        Optional<Person> optionalPerson = peopleRepository.findById(personId);

        optionalBook.ifPresent(book -> book.setPerson(optionalPerson.orElse(null)));
        optionalBook.ifPresent(book -> book.setTakenAt(new Date()));
        optionalPerson.ifPresent(person -> person.getBooks().add(optionalBook.orElse(null)));
    }

    @Transactional
    public void release(int id) {
        Optional<Book> optionalBook = booksRepository.findById(id);

        optionalBook.ifPresent(book -> {
            Person person = book.getPerson();

            book.setPerson(null);
            book.setTakenAt(null);
            book.setExpired(false);
            person.getBooks().remove(book);
        });
    }

    public List<Book> findByTitleStartingWith(String title) {
        List<Book> books = title.isEmpty() ? new ArrayList<>() : booksRepository.findByTitleStartingWith(StringUtils.capitalize(title.toLowerCase()));
        books.stream().map(Book::getPerson).forEach(Hibernate::initialize);

        return books;
    }
}
