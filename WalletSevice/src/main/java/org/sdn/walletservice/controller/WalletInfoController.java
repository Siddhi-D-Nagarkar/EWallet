package org.sdn.walletservice.controller;


import org.sdn.walletservice.dto.WalletResponseDTO;
import org.sdn.walletservice.entity.Wallet;
import org.sdn.walletservice.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WalletInfoController {

    @Autowired
    private WalletService walletService;
    @GetMapping("/userWalletDetails")
    public ResponseEntity<WalletResponseDTO> userWalletDetails(@RequestParam("contactNo") String contactNo,
                                                               @RequestParam("type") String type,
                                                               @RequestParam(value = "amount",required = false) Double amount
    ) {
        WalletResponseDTO wallet = walletService.walletDetails(contactNo,type,amount);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }
}
