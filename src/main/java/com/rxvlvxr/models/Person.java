package com.rxvlvxr.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @NotBlank(message = "Поле имени должно быть заполнено")
    @Size(min = 2, max = 100, message = "Имя должно быть в диапазоне от 2 до 100 символов")
    @Pattern(regexp = "[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+", message = "Пример ввода: Габараев Рауль Зурабович")
    @Column(name = "name")
    private String name;
    @Min(value = 1886, message = "Год рождения должен быть выше 1885")
    @Column(name = "birth_year")
    private int birthYear;
    @OneToMany(mappedBy = "person")
    private List<Book> books;

    public Person() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
