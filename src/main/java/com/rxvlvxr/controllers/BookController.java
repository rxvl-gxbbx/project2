package com.rxvlvxr.controllers;

import com.rxvlvxr.models.Book;
import com.rxvlvxr.models.Person;
import com.rxvlvxr.services.BooksService;
import com.rxvlvxr.services.PeopleService;
import com.rxvlvxr.util.BookValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BooksService booksService;
    private final PeopleService peopleService;
    private final BookValidator bookValidator;

    // внедрение зависимостей
    @Autowired
    public BookController(BooksService booksService, PeopleService peopleService, BookValidator bookValidator) {
        this.booksService = booksService;
        this.peopleService = peopleService;
        this.bookValidator = bookValidator;
    }

    // GET запрос, который возвращает страницу index.html со всеми данными из таблицы book
    @GetMapping
    public String index(Model model,
                        // указываем что ждем необязательный параметр в запросе page
                        // с помощью него можно указать страницу
                        @RequestParam(name = "page", required = false) Optional<Integer> page,
                        // параметр для отображения фиксированного количества строк из таблицы
                        @RequestParam(name = "books_per_page", required = false) Optional<Integer> booksPerPage,
                        // параметр для сортировки
                        @RequestParam(name = "sort_by_year", required = false, defaultValue = "true") boolean sortByYear) {
        model.addAttribute("books", booksService.findAll(page, booksPerPage, sortByYear));

        return "books/index";
    }

    // POST запрос, который при успешной валидации сохраняет объект типа Book в таблицу
    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        String view;

        // валидация
        bookValidator.validate(book, bindingResult);

        // в случае ошибок будет возвращена страница добавления книги с требованиям валидации
        if (bindingResult.hasErrors()) view = "books/new";
        else {
            booksService.save(book);
            view = "redirect:/books";
        }

        return view;
    }

    // возвращает страницу для добавления книги
    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    // возвращает страницу редактирования книги по id
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        booksService.findById(id).ifPresent(value -> model.addAttribute("book", value));

        return "books/edit";
    }

    // возвращает страницу книги по id
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("anotherPerson") Person anotherPerson) {
        booksService.findById(id).ifPresent(book -> model.addAttribute("book", book));
        // в случае если книга находится у человека, то вернется держатель книги
        // если нет, то будет возвращен список всех доступных людей для назначения книги
        booksService.findPersonByBookId(id).ifPresentOrElse(
                person -> model.addAttribute("person", person),
                () -> model.addAttribute("people", peopleService.findAll()));

        return "books/show";
    }

    // PATCH запрос для редактирования книги
    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id,
                         @ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        String view;

        bookValidator.validate(book, bindingResult);

        if (bindingResult.hasErrors()) view = "books/edit";
        else {
            booksService.update(id, book);
            view = "redirect:/books";
        }

        return view;
    }

    // PATCH запрос при котором книге присваивается человек
    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int bookId,
                         @ModelAttribute("anotherPerson") Person person) {
        int personId = person.getId();

        booksService.assignToPerson(bookId, personId);

        return "redirect:/books";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        booksService.release(id);

        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksService.delete(id);

        return "redirect:/books";
    }

    // получаем страницу поиска
    @GetMapping("/search")
    public String searchPage() {
        return "books/search";
    }

    // POST запрос для поиска книги по набору букв, переданному в параметр запроса
    @PostMapping("/search")
    public String searchResults(
            // параметр по которому Hibernate ищет совпадения в таблице
            @RequestParam(name = "title", required = false) String title, Model model) {
        if (title != null) model.addAttribute("books", booksService.findByTitleStartingWith(title));

        return "books/search";
    }
}
