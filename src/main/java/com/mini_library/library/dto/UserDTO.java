package com.mini_library.library.dto;

import com.mini_library.library.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String libraryId;
    private String email;
    private String roleType;

    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setLibraryId(user.getLibraryId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRoleType(user.getRole().getRole().name());
        return userDTO;
    }


}