package com.mini_library.library.repository;

import com.mini_library.library.model.Book;
import com.mini_library.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByIsbn(String isbn);

    List<Book> findAllByBorrowed(boolean borrowed);

    Book getBooksByUser(User user);
}
