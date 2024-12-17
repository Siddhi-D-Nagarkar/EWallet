package org.sdn.userservice.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.sdn.userservice.dto.request.UserRequestDTO;
import org.sdn.userservice.dto.response.UserResponseDTO;
import org.sdn.userservice.entity.User;
import org.sdn.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/addUpdate")
    public ResponseEntity<UserResponseDTO> addUpdate(@RequestBody @Validated UserRequestDTO userRequestDTO) throws JsonProcessingException {
        User user = userService.addUpdate(userRequestDTO);
        if(user != null) {
            UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                    .address(user.getAddress())
                    .email(user.getEmail())
                    .phoneNo(user.getPhoneNo())
                    .identifier(user.getIdentifier())
                    .name(user.getName())
                    .build();
            return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
    }
}
