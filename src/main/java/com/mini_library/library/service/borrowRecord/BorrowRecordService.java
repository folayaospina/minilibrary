package com.mini_library.library.service.borrowRecord;

import com.mini_library.library.model.Book;
import com.mini_library.library.model.BorrowRecord;
import com.mini_library.library.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class BorrowRecordService {

    public BorrowRecord createBorrowRecord(LocalDateTime borrowDate, LocalDateTime dueDate, LocalDateTime returnDate, User user, Book book) {
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setBorrowDate(borrowDate);
        borrowRecord.setDueDate(dueDate);
        borrowRecord.setReturnDate(returnDate);
        return borrowRecord;
    }
}
