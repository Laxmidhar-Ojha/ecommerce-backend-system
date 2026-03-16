package com.ecommerce.dto;

import com.ecommerce.entity.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    private String name;
    private String email;
    private String password;
    private Role role;
}