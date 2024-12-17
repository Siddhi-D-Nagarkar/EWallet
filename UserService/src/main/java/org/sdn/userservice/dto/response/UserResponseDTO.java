package org.sdn.userservice.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.sdn.userservice.entity.UserIdentifier;

@Getter
@Setter
@Builder
public class UserResponseDTO {
    private String name;
    private String email;
    private String phoneNo;
    private String address;
    private UserIdentifier identifier;
}
