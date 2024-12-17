package org.sdn.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.sdn.userservice.entity.User;
import org.sdn.userservice.entity.UserIdentifier;

@Setter
@Getter
public class UserRequestDTO {
    private String name;
    @NotBlank(message = "email should not be blank")
    private String email;
    @NotBlank(message = "password should not be blank")
    private String password;
    private String phoneNo;
    private String address;
    @NotNull(message = "userIdentifier will be required")
    private UserIdentifier identifier;
    @NotBlank(message = "userIdentifierValue will be required")
    private String userIdentifierValue;

    public User toUser() {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .phoneNo(this.phoneNo)
                .address(this.address)
                .identifier(this.identifier)
                .userIdentifierValue(this.userIdentifierValue)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .build();
    }
}
