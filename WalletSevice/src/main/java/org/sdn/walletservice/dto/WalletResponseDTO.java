package org.sdn.walletservice.dto;

import lombok.*;
import org.sdn.walletservice.entity.Wallet;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponseDTO {


    private String message;
    private String error;
    private Wallet wallet;
}
