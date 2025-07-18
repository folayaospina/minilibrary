package com.mini_library.library.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookDTO {
    private String title;
    private String author;
    private String isbn;
    private boolean borrowed;

    public static BookDTO fromBook(com.mini_library.library.model.Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setIsbn(book.getIsbn());
        bookDTO.setBorrowed(book.isBorrowed());
        return bookDTO;
    }
}
