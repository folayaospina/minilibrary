package com.mini_library.library.model;

import com.mini_library.library.interfaces.IRole;
import com.mini_library.library.model.auth.Token;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String email;
    private String libraryId;
    private String password;
    
    

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Token> tokens;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Book> books;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BorrowRecord> borrowRecords;
}
