package org.sdn.userservice.dto.request;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserValidationRequestDTO {
     private String TxnAmount;
     private String message;
     private String receiverContactNumber;
}
