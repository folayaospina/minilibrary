package com.mini_library.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "borrow_records")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;

    private LocalDateTime returnDate;

    @Column(nullable = false)
    private boolean active = true;

    @PrePersist
    protected void onCreate() {
        borrowDate = LocalDateTime.now();
    }

}

