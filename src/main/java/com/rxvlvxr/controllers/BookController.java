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

    @Autowired
    public BookController(BooksService booksService, PeopleService peopleService, BookValidator bookValidator) {
        this.booksService = booksService;
        this.peopleService = peopleService;
        this.bookValidator = bookValidator;
    }

    @GetMapping
    public String index(Model model,
                        @RequestParam(name = "page", required = false) Optional<Integer> page,
                        @RequestParam(name = "books_per_page", required = false) Optional<Integer> booksPerPage,
                        @RequestParam(name = "sort_by_year", required = false, defaultValue = "true") boolean sortByYear) {
        model.addAttribute("books", booksService.findAll(page, booksPerPage, sortByYear));

        return "books/index";
    }

    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        String view;

        bookValidator.validate(book, bindingResult);

        if (bindingResult.hasErrors()) view = "books/new";
        else {
            booksService.save(book);
            view = "redirect:/books";
        }

        return view;
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        booksService.findById(id).ifPresent(value -> model.addAttribute("book", value));

        return "books/edit";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("anotherPerson") Person anotherPerson) {
        booksService.findById(id).ifPresent(book -> model.addAttribute("book", book));
        booksService.findPersonByBookId(id).ifPresentOrElse(
                person -> model.addAttribute("person", person),
                () -> model.addAttribute("people", peopleService.findAll()));

        return "books/show";
    }

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

    @GetMapping("/search")
    public String searchPage() {
        return "books/search";
    }

    @PostMapping("/search")
    public String searchResults(@RequestParam(name = "title", required = false) String title, Model model) {
        if (title != null) model.addAttribute("books", booksService.findByTitleStartingWith(title));

        return "books/search";
    }
}
