package com.mini_library.library.service.book;

import com.mini_library.library.controller.book.request.BookRequest;
import com.mini_library.library.dto.BookDTO;
import com.mini_library.library.dto.BorrowRecordDTO;
import com.mini_library.library.model.Book;
import com.mini_library.library.model.BorrowRecord;
import com.mini_library.library.model.User;
import com.mini_library.library.repository.BookRepository;
import com.mini_library.library.repository.BorrowRecordRepository;
import com.mini_library.library.repository.UserRepository;
import com.mini_library.library.service.borrowRecord.BorrowRecordService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final BorrowRecordService borrowRecordService;

    private LocalDateTime calculateDueDate() {
        return LocalDateTime.now().plusDays(15L);
    }


    @Transactional
    public void borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));

        if (book.isBorrowed()) {
            throw new IllegalStateException("Book is already borrowed");
        }

        if (user.getBooks().size() == 2) {
            throw new IllegalStateException("User has reached the maximum number of books");
        }

        BorrowRecord borrowRecord = borrowRecordService.createBorrowRecord(LocalDateTime.now(), calculateDueDate(), null, user, book);
        borrowRecordRepository.save(borrowRecord);
        book.setUser(user);
        book.setBorrowed(true);
        bookRepository.save(book);
    }

    @Transactional
    public String returnBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));

        if (!book.isBorrowed()) {
            throw new IllegalStateException("Book is not borrowed");
        }
        BorrowRecord borrowRecord = borrowRecordService.createBorrowRecord(null, null, LocalDateTime.now(), user, book);

        borrowRecordRepository.save(borrowRecord);
        book.setBorrowed(false);
        book.setUser(null);
        bookRepository.save(book);
        return book.getIsbn();
    }

    @Transactional
    public ResponseEntity<Book> createBook(BookRequest newBook, Long userId) {
        validateBookRequest(newBook);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (isLibrarian(user)) {
            throw new RuntimeException("User is not a librarian");
        }

        Book existingBook = bookRepository.findByIsbn(newBook.isbn());
        if (existingBook != null) {
            throw new IllegalStateException("A book with ISBN " + newBook.isbn() + " already exists");

        }

        Book bookToSave = convertToBook(newBook);
        Book savedBook = bookRepository.save(bookToSave);
        return ResponseEntity.ok(savedBook);
    }

    @Transactional
    public ResponseEntity<Book> updateBook(BookRequest bookRequest, Long userId, Long bookId) {

        User librarian = findUserById(userId);
        validateLibrarianRole(librarian);

        Book bookToUpdate = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado"));

        Book bookWithSameIsbn = bookRepository.findByIsbn(bookRequest.isbn());
        if (bookWithSameIsbn != null && !bookWithSameIsbn.getId().equals(bookId)) {
            throw new RuntimeException("The ISBN already exist" + bookRequest.isbn());
        }

        if (!bookRequest.title().isEmpty() ) {
        bookToUpdate.setTitle(bookRequest.title());
        }else{
            bookToUpdate.setTitle(bookToUpdate.getTitle());
        }

        if (!bookRequest.author().isEmpty()){
        bookToUpdate.setAuthor(bookRequest.author());
        }else{
            bookToUpdate.setAuthor(bookToUpdate.getAuthor());
        }
        if (!bookRequest.isbn().isEmpty()) {
            bookToUpdate.setIsbn(bookRequest.isbn());
        }else{
            bookToUpdate.setIsbn(bookToUpdate.getIsbn());
        }

        Book updatedBook = bookRepository.save(bookToUpdate);
        return ResponseEntity.ok(updatedBook);

    }

    @Transactional
    public ResponseEntity<Book> deleteBook(String bookISBN, Long userId) {
        User librarian = findUserById(userId);
        validateLibrarianRole(librarian);
        Book bookToDelete = bookRepository.findByIsbn(bookISBN);
        if (bookToDelete == null) {
            throw new EntityNotFoundException("Libro no encontrado");
        }
        bookRepository.delete(bookToDelete);
        return ResponseEntity.ok(bookToDelete);
    }

    @Transactional
    public ResponseEntity<List<BookDTO>> searchBook(BookRequest bookRequest) {
        if (bookRequest == null) {
            throw new IllegalArgumentException("Book data cannot be null");
        }

        List<Book> foundBooks = bookRepository.findAll().stream()
                .filter(book ->
                        (bookRequest.title() == null || book.getTitle().toLowerCase().contains(bookRequest.title().toLowerCase())) &&
                                (bookRequest.author() == null || book.getAuthor().toLowerCase().contains(bookRequest.author().toLowerCase())) &&
                                (bookRequest.isbn() == null || book.getIsbn().equals(bookRequest.isbn())))
                .toList();

        if (foundBooks.isEmpty()) {
            throw new EntityNotFoundException("No books found matching the criteria");
        }

        List<BookDTO> bookDTOs = foundBooks.stream()
                .map(BookDTO::fromBook)
                .toList();

        return ResponseEntity.ok(bookDTOs);
    }

    @Transactional
    public ResponseEntity<List<BookDTO>> borrowedBooks() {
        List<Book> booksBorrowed = bookRepository.findAllByBorrowed(true);

        if (booksBorrowed.isEmpty()) {
            throw new RuntimeException("No books borrowed");
        }

        List<BookDTO> booksBorrowedDTO = booksBorrowed.stream()
                .map(BookDTO::fromBook)
                .toList();

        return ResponseEntity.ok(booksBorrowedDTO);
    }

    @Transactional
    public ResponseEntity<List<BookDTO>> booksBorrowedByUser(Long userId) {
        User user = findUserById(userId);
        if (user.getBooks().isEmpty()) {
            throw new RuntimeException("User has no books borrowed");
        }

        List<BookDTO> bookDTOList = user.getBooks().stream()
                .map(BookDTO::fromBook)
                .toList();

        return ResponseEntity.ok(bookDTOList);
    }

    @Transactional
    public ResponseEntity<List<BorrowRecordDTO>> booksBorrowHistoryByUser(Long userId) {
        User user = findUserById(userId);
        if (user.getBooks().isEmpty()) {
            throw new RuntimeException("User has no books borrowed");
        }

        List<BorrowRecordDTO> borrowRecList = user.getBorrowRecords().stream()
                .map(BorrowRecordDTO::fromBorrowRecord)
                .collect(Collectors.toList());

        return ResponseEntity.ok(borrowRecList);
    }

    
    
    
    private void validateBookRequest(BookRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Book data cannot be null");
        }
        if (request.isbn() == null || request.isbn().trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN is required");
        }
    }

    private Book convertToBook(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setIsbn(request.isbn());
        book.setBorrowed(request.borrowed());
        return book;
    }

    private boolean isLibrarian(User user) {
        return user.getRole().getRole().toString().equals("USER");
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    private void validateLibrarianRole(User user) {
        if (user.getRole().getRole().toString().equals("USER")) {
            throw new RuntimeException("El usuario no es bibliotecario");
        }
    }


}
