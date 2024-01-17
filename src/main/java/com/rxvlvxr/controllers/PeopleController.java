package com.rxvlvxr.controllers;

import com.rxvlvxr.models.Person;
import com.rxvlvxr.services.PeopleService;
import com.rxvlvxr.util.PersonValidator;
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
    private final PersonValidator personValidator;
    private final PersonYearValidator personYearValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator, PersonYearValidator personYearValidator) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
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
        personValidator.validate(person, bindingResult);
        personYearValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            return "people/new";

        peopleService.save(person);

        return "redirect:/people";
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
        personYearValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            return "people/edit";

        peopleService.update(id, person);

        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        peopleService.delete(id);

        return "redirect:/people";
    }
}
