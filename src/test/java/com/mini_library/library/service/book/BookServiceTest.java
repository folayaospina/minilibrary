package com.mini_library.library.service.book;

import com.mini_library.library.model.Book;
import com.mini_library.library.model.BorrowRecord;
import com.mini_library.library.model.User;
import com.mini_library.library.repository.BookRepository;
import com.mini_library.library.repository.BorrowRecordRepository;
import com.mini_library.library.repository.UserRepository;
import com.mini_library.library.service.borrowRecord.BorrowRecordService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private BorrowRecordService borrowRecordService;

    @Test
    @Transactional
    void borrowBook_ShouldSuccessfullyBorrowWhenConditionsMet() {
        Long userId = 1L;
        Long bookId = 1L;

        User user = new User();
        user.setId(userId);

        Book book = new Book();
        user.setBooks(new ArrayList<>());

        book.setId(bookId);
        book.setBorrowed(false);

        BorrowRecord borrowRecord = new BorrowRecord();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Mockito.when(borrowRecordService.createBorrowRecord(any(), any(), any(), eq(user), eq(book)))
                .thenReturn(borrowRecord);

        bookService.borrowBook(userId, bookId);

        verify(borrowRecordRepository, times(1)).save(borrowRecord);
        verify(bookRepository, times(1)).save(book);
        assertTrue(book.isBorrowed());
        assertEquals(user, book.getUser());
    }

    @Test
    void borrowBook_ShouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;
        Long bookId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> bookService.borrowBook(userId, bookId));

        assertEquals("User not found", exception.getMessage());
        verify(bookRepository, never()).findById(any());
        verifyNoInteractions(borrowRecordRepository);
    }

    @Test
    void borrowBook_ShouldThrowExceptionWhenBookNotFound() {
        Long userId = 1L;
        Long bookId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> bookService.borrowBook(userId, bookId));

        assertEquals("Book not found", exception.getMessage());
        verifyNoInteractions(borrowRecordRepository);
    }

    @Test
    void borrowBook_ShouldThrowExceptionWhenBookAlreadyBorrowed() {
        Long userId = 1L;
        Long bookId = 1L;

        User user = new User();
        user.setId(userId);

        Book book = new Book();
        book.setId(bookId);
        book.setBorrowed(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> bookService.borrowBook(userId, bookId));

        assertEquals("Book is already borrowed", exception.getMessage());
        verifyNoInteractions(borrowRecordRepository);
    }

    @Test
    void borrowBook_ShouldThrowExceptionWhenUserHasMaximumBooks() {
        Long userId = 1L;
        Long bookId = 1L;

        User user = new User();
        user.setId(userId);

        user.setBooks(new ArrayList<>());
        user.getBooks().add(new Book());
        user.getBooks().add(new Book());

        Book book = new Book();
        book.setId(bookId);
        book.setBorrowed(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> bookService.borrowBook(userId, bookId));

        assertEquals("User has reached the maximum number of books", exception.getMessage());
        verifyNoInteractions(borrowRecordRepository);

    }
}