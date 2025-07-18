package com.mini_library.library.controller.user;

import com.mini_library.library.controller.book.request.BookRequest;
import com.mini_library.library.dto.BookDTO;
import com.mini_library.library.dto.BorrowRecordDTO;
import com.mini_library.library.dto.UserDTO;
import com.mini_library.library.model.Book;
import com.mini_library.library.model.BorrowRecord;
import com.mini_library.library.service.book.BookService;
import com.mini_library.library.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final BookService bookService;


    /**
     * Allows a user to borrow a book based on their user ID and the book ID.
     *
     * @param userId the ID of the user attempting to borrow the book
     * @param bookId the ID of the book to be borrowed
     * @return a ResponseEntity containing a success message if the book is successfully borrowed,
     *         a bad request response with an error message if the book is already borrowed or the user has reached
     *         their borrowing limit, or a not found response if the user or book is not found
     */
    @PostMapping("/{userId}/borrow/{bookId}")
    public ResponseEntity<String> borrowBook(
            @PathVariable Long userId,
            @PathVariable Long bookId
    ) {
        try {
            bookService.borrowBook(userId, bookId);
            return ResponseEntity.ok("Book Borrowed Successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Handles the return of a book by a user. It updates the borrowing status of the book
     * and creates a borrow record to log the return operation.
     *
     * @param userId the ID of the user returning the book
     * @param bookId the ID of the book being returned
     * @return a ResponseEntity containing a success message if the book is returned successfully,
     *         a bad request error message if the book cannot be returned, or a not found response
     *         if the user or book does not exist
     */
    @PutMapping("/{userId}/return/{bookId}")
    public ResponseEntity<String> returnBook(
            @PathVariable Long userId,
            @PathVariable Long bookId
    ) {
        try {
            String isbn = bookService.returnBook(userId, bookId);
            return ResponseEntity.ok("The Book, ISBN: " + isbn + ", returned Successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves a list of books currently borrowed by a specified user.
     *
     * @param userId the ID of the user whose borrowed books are to be retrieved
     * @return a ResponseEntity containing a list of BookDTO objects representing
     *         the books borrowed by the user
     */
    @GetMapping("/{userId}/books")
    public ResponseEntity<List<BookDTO>> booksBorrowedByUser(
            @PathVariable Long userId
    ){
        return bookService.booksBorrowedByUser(userId);
    }

    /**
     * Updates the details of a specific book identified by its ID. Only users
     * with librarian roles are allowed to update book information. If the provided
     * ISBN already exists for another book, an exception is thrown.
     *
     * @param book   the details of the book to update, including title, author, ISBN, and borrowed status
     * @param userId the ID of the user attempting to update the book
     * @param bookId the ID of the book to be updated
     * @return a ResponseEntity containing the updated book details
     */
    @PostMapping("/{userId}/book/{bookId}/update")
    public ResponseEntity<Book> updateBook(
            @RequestBody  BookRequest book,
            @PathVariable Long userId,
            @PathVariable Long bookId
    ){
        return bookService.updateBook(book, userId, bookId);
    }



    /**
     * Retrieves the borrow history of books for a specific user.
     *
     * @param userId the ID of the user whose borrow history is to be retrieved
     * @return a ResponseEntity containing a list of BorrowRecordDTO objects representing the borrow history of the user
     */
    @PostMapping("/{userId}/history")
    public ResponseEntity<List<BorrowRecordDTO>> booksBorrowHistoryByUser(
            @PathVariable Long userId
    ){
        return bookService.booksBorrowHistoryByUser(userId);
    }

}

