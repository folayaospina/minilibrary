package com.mini_library.library.controller.auth;

public record LoginRequest(
        String email,
        String password
) {
}
