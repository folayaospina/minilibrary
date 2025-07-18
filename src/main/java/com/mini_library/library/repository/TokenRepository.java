package com.mini_library.library.repository;

import com.mini_library.library.model.auth.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllValidIsFalseOrRevokedIsFalseByUser_Email(String userEmail);

    Token findTokenByToken(String token);
}