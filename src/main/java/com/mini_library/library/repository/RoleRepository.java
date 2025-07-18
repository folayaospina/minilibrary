package com.mini_library.library.repository;

import com.mini_library.library.interfaces.IRole;
import com.mini_library.library.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findRoleByRole(IRole role);
}
