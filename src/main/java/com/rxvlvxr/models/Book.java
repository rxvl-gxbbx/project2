package com.rxvlvxr.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @NotBlank(message = "Поле названия книги должно быть заполнено")
    @Size(min = 2, max = 255, message = "Название должно быть в диапазоне от 2 до 100 символов")
    @Pattern(regexp = "[A-ZА-ЯЁ][a-zа-яё]+[\\sA-zА-яё]*", message = "Пример ввода: Солярис")
    @Column(name = "title")
    private String title;
    @NotBlank(message = "Поле автора должно быть заполнено")
    @Size(min = 2, max = 100, message = "Имя должно быть в диапазоне от 2 до 100 символов")
    @Pattern(regexp = "[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+", message = "Пример ввода: Станислав Лем")
    @Column(name = "author")
    private String author;
    @Min(value = 1801, message = "Год должен быть больше 1800")
    @Column(name = "year")
    private int year;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;
    @Column(name = "taken_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date takenAt;
    @Transient
    private boolean expired;

    public Book() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(Date takenAt) {
        this.takenAt = takenAt;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
