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

// сервис для сущности book, здесь реализуется вся бизнес-логика
@Service
@Transactional(readOnly = true)
public class BooksService {
    private final BooksRepository booksRepository;
    private final PeopleRepository peopleRepository;

    // внедряем зависимости
    @Autowired
    public BooksService(BooksRepository booksRepository, PeopleRepository peopleRepository) {
        this.booksRepository = booksRepository;
        this.peopleRepository = peopleRepository;
    }

    // возвращает список объекта Book в соответсвии с указанными параметрами пагинации и сортировки
    public List<Book> findAll(Optional<Integer> page, Optional<Integer> booksPerPage, boolean sortByYear) {
        // если параметр sortByYear == true, то будет производиться сортировка по году
        Sort sortCriteria = sortByYear ? Sort.by("year") : Sort.unsorted();
        // если передан параметр, то он будет присвоен переменной itemsPerPage
        // в ином случае переменной присвоится максимальное значение объекта Integer
        int itemsPerPage = booksPerPage.orElse(Integer.MAX_VALUE);
        // та же логика что и у переменной itemsPerPage
        int pageCount = page.orElse(0);

        return booksRepository.findAll(PageRequest.of(pageCount, itemsPerPage, sortCriteria)).getContent();
    }

    // узнаем есть ли объект Book по переданному id
    public Optional<Book> findById(int id) {
        return booksRepository.findById(id);
    }

    // сохраняем объект типа Book в таблицу
    // указываем аннотацию, так как хотим присвоить параметру readonly дефолтное значение false
    // т.к. здесь мы добавляем стркоу в таблицу
    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    // редактируем данные в таблице book по id
    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        Optional<Book> optionalBook = booksRepository.findById(id);

        optionalBook.ifPresent(book -> {
            Person person = book.getPerson();

            if (person != null) {
                // делаем явные присваивания (для кэша Hibernate)
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

    // получаем объект типа Person по id сущности book
    public Optional<Person> findPersonByBookId(int id) {
        final Person[] person = {null};
        Optional<Book> optionalBook = booksRepository.findById(id);

        optionalBook.ifPresent(book -> {
            // делаем инициализации Hibernate объекта, чтобы он перешел из transient в состояние persistent
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

    // возвращает список объектов Book, которые соответствуют параметру title
    // (ищем соответствие этого параметра с первыми буквами столбца title в сущности book)
    public List<Book> findByTitleStartingWith(String title) {
        // если передано пустое значение, то присвоится пустой список, в ином случае будет поиск по первым буквам названия книги
        List<Book> books = title.isEmpty() ? new ArrayList<>() : booksRepository.findByTitleStartingWith(StringUtils.capitalize(title.toLowerCase()));
        // явно инициализируем Hibernate объект
        books.stream().map(Book::getPerson).forEach(Hibernate::initialize);

        return books;
    }
}
