package com.rxvlvxr.controllers;

import com.rxvlvxr.models.Person;
import com.rxvlvxr.services.PeopleService;
import com.rxvlvxr.util.PersonNameValidator;
import com.rxvlvxr.util.PersonYearValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;
    private final PersonNameValidator personNameValidator;
    private final PersonYearValidator personYearValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonNameValidator personNameValidator, PersonYearValidator personYearValidator) {
        this.peopleService = peopleService;
        this.personNameValidator = personNameValidator;
        this.personYearValidator = personYearValidator;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", peopleService.findAll());

        return "people/index";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {
        String view;

        personNameValidator.validate(person, bindingResult);
        personYearValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            view = "people/new";
        else {
            peopleService.save(person);
            view = "redirect:/people";
        }

        return view;
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        peopleService.findById(id).ifPresent(value -> model.addAttribute("person", value));

        return "people/edit";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("books", peopleService.findAllBooksByPersonId(id));
        peopleService.findById(id).ifPresent(person -> model.addAttribute("person", person));

        return "people/show";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        String view;

        personYearValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            view = "people/edit";
        else {
            peopleService.update(id, person);
            view = "redirect:/people";
        }
        return view;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        peopleService.delete(id);

        return "redirect:/people";
    }
}
