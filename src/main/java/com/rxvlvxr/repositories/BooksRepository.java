package com.rxvlvxr.repositories;

import com.rxvlvxr.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {
    @Override
    Page<Book> findAll(Pageable pageable);

    @Override
    List<Book> findAll(Sort sort);

    List<Book> findByTitleStartingWith(String title);
}
