package org.sdn.userservice.controller;


import org.json.simple.JSONObject;
import org.sdn.userservice.dto.request.UserValidationRequestDTO;
import org.sdn.userservice.dto.response.WalletResponseDTO;
import org.sdn.userservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
public class UserValidationController {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/start-txn")
    public String validateUserBeforeTxn(@RequestBody UserValidationRequestDTO userValidationRequestDTO,
                                        @AuthenticationPrincipal User user) {
        //sender has the wallet available
        URI uriForSenderInfo = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8090/userWalletDetails")
                .queryParam("contactNo",user.getPhoneNo())
                .build().toUri();

        ResponseEntity<WalletResponseDTO> senderInfo = restTemplate.exchange(uriForSenderInfo, HttpMethod.GET, HttpEntity.EMPTY, WalletResponseDTO.class);

        URI uriForReceiverInfo = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8090/userWalletDetails")
                .queryParam("contactNo",userValidationRequestDTO.getReceiverContactNumber())
                .build().toUri();

        ResponseEntity<WalletResponseDTO> receiverInfo= restTemplate.exchange(uriForReceiverInfo, HttpMethod.GET, HttpEntity.EMPTY, WalletResponseDTO.class);

        if(!receiverInfo.getBody().getMessage().equalsIgnoreCase("success")){
            return receiverInfo.getBody().getError();
        }
        if(!senderInfo.getBody().getMessage().equalsIgnoreCase("success")){
            return senderInfo.getBody().getError();
        }


        //Send to kafka

        return "txn has been started,will be sending the confirmation soon";
    }
}
