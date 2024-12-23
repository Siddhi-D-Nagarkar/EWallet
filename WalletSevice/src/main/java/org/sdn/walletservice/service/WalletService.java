package org.sdn.walletservice.service;

import org.sdn.walletservice.dto.WalletResponseDTO;
import org.sdn.walletservice.entity.TxnRequestType;
import org.sdn.walletservice.entity.Wallet;
import org.sdn.walletservice.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;


    public WalletResponseDTO walletDetails(String contactNo, String type, double amount) {
        Wallet wallet = walletRepository.findByContact(contactNo);
        if (wallet == null) {
            return WalletResponseDTO.builder()
                    .message("Failed")
                    .error("Wallet not found for sender")
                    .wallet(null)
                    .build();
        }

        //sender or receiver is been called
        if(TxnRequestType.SENDER.equals(type)) {
            return WalletResponseDTO.builder()
                    .message("failed")
                    .error("Insufficient Funds")
                    .wallet(null)
                    .build();
        }

        return WalletResponseDTO.builder()
                .message("success")
                .error(null)
                .wallet(null)
                .build();
    }
}
