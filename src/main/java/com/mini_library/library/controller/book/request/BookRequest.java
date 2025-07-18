package com.mini_library.library.controller.book.request;

public record BookRequest(
        String title,
        String author,
        String isbn,
        boolean borrowed
) {
}
