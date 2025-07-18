package com.mini_library.library.dto;

import com.mini_library.library.model.Book;
import com.mini_library.library.model.BorrowRecord;
import com.mini_library.library.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordDTO {
    private String bookName;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private boolean active;

    public static BorrowRecordDTO fromBorrowRecord(BorrowRecord borrowRecord) {
        BorrowRecordDTO borrowRecordDTO = new BorrowRecordDTO();
        borrowRecordDTO.setBookName(borrowRecord.getBook().getTitle());
        borrowRecordDTO.setBorrowDate(borrowRecord.getBorrowDate());
        borrowRecordDTO.setDueDate(borrowRecord.getDueDate());
        borrowRecordDTO.setReturnDate(borrowRecord.getReturnDate());
        borrowRecordDTO.setActive(borrowRecord.isActive());
        return borrowRecordDTO;
    }



}
