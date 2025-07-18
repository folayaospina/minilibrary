package com.mini_library.library.controller.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mini_library.library.interfaces.IRole;
import com.mini_library.library.model.Role;


public record RegisterRequest(
        String name,
        String email,
        String libraryId,
        String password,
        IRole roleType
){
}
