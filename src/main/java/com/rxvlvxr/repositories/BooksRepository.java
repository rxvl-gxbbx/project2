package com.rxvlvxr.repositories;

import com.rxvlvxr.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// указываем что это репозиторий
@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {
    // добавляем нестандартный метод для поиска строки начало названия которой совпадает с параметром title
    List<Book> findByTitleStartingWith(String title);
}
