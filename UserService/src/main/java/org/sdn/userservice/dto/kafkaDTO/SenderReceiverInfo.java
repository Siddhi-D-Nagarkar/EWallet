package org.sdn.userservice.dto.kafkaDTO;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SenderReceiverInfo {

    private String senderContact;
    private String receiverContact;
    private String messageFromSender;
    private double amount;
}
