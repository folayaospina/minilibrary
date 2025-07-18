package com.mini_library.library.controller.book;

import com.mini_library.library.controller.book.request.BookRequest;
import com.mini_library.library.dto.BookDTO;
import com.mini_library.library.model.Book;
import com.mini_library.library.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * Creates a new book entry in the system for a specific user.
     *
     * @param book   the details of the book to be created, including title, author, ISBN, and borrowed status
     * @param userId the ID of the user associated with the book creation
     * @return a {@code ResponseEntity} containing the created {@code Book} instance
     */
    @PostMapping("/create/{userId}")
    public ResponseEntity<Book> createBook(@Validated @RequestBody final BookRequest book, @PathVariable Long userId) {
        return bookService.createBook(book, userId);
    }
    /**
     * Deletes a book identified by its ISBN for a specific user identified by their user ID.
     * This operation is restricted to users with a librarian role.
     *
     * @param userId    the ID of the user making the request; must be a librarian
     * @param bookISBN  the ISBN of the book to be deleted
     * @return a ResponseEntity containing the deleted Book object if the operation is successful
     */
    @PostMapping("/user/{userId}/delete/{bookISBN}")
    public ResponseEntity<Book> deleteBook(@PathVariable Long userId, @PathVariable String bookISBN) {
        return bookService.deleteBook(bookISBN, userId);
    }

    /**
     * Retrieves all books that are currently marked as borrowed.
     *
     * @return a ResponseEntity containing a list of borrowed books.
     */
    @GetMapping("/borrowed")
    public ResponseEntity<List<Book>> getAllBorrowedBooks() {
        return bookService.borrowedBooks();
    }

    /**
     * Retrieves a list of books associated with a user based on the filtering criteria provided in the request.
     *
     * @param book the filtering criteria used to search for books, including fields such as title, author, ISBN, and borrowed status.
     * @return a ResponseEntity containing a list of BookDTO objects that match the search criteria.
     */
    @PostMapping("/search")
    public ResponseEntity<List<BookDTO>> getAllBooksByUser(@Validated @RequestBody final BookRequest book) {
        return bookService.searchBook(book);
    }



}
