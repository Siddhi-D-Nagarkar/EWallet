package org.sdn.userservice.controller;


import org.json.simple.JSONObject;
import org.sdn.commonservice.commonutilities.Constants;
import org.sdn.userservice.dto.kafkaDTO.SenderReceiverInfo;
import org.sdn.userservice.dto.request.UserValidationRequestDTO;
import org.sdn.userservice.dto.response.WalletResponseDTO;
import org.sdn.userservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    @Qualifier("txnCreatedTopicKafkaTemplate")
    private KafkaTemplate<String, SenderReceiverInfo> kafkaTemplate;


    @PostMapping("/start-txn")
    public String validateUserBeforeTxn(@RequestBody UserValidationRequestDTO userValidationRequestDTO,
                                        @AuthenticationPrincipal User user) {
        //sender has the wallet available
        URI uriForSenderInfo = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8090/userWalletDetails")
                .queryParam("contactNo",user.getPhoneNo())
                .queryParam("type","sender")
                .queryParam("amount",userValidationRequestDTO.getTxnAmount())
                .build().toUri();

        ResponseEntity<WalletResponseDTO> senderInfo = restTemplate.
                exchange(uriForSenderInfo, HttpMethod.GET, null, WalletResponseDTO.class);

        URI uriForReceiverInfo = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8090/userWalletDetails")
                .queryParam("contactNo",userValidationRequestDTO.getReceiverContactNumber())
                .queryParam("type","receiver")
                .build().toUri();

        ResponseEntity<WalletResponseDTO> receiverInfo= restTemplate
                .exchange(uriForReceiverInfo, HttpMethod.GET, null, WalletResponseDTO.class);

        if(!receiverInfo.getBody().getMessage().equalsIgnoreCase("success")){
            return receiverInfo.getBody().getError();
        }
        if(!senderInfo.getBody().getMessage().equalsIgnoreCase("success")){
            return senderInfo.getBody().getError();
        }


        //Send to kafka
        kafkaTemplate.send(Constants.TXN_INITIATED_TOPIC,SenderReceiverInfo.builder()
                .senderContact(user.getPhoneNo())
                .receiverContact(userValidationRequestDTO.getReceiverContactNumber())
                .messageFromSender(userValidationRequestDTO.getMessage())
                .amount(userValidationRequestDTO.getTxnAmount())
                .build());

        return "txn has been started,will be sending the confirmation soon";
    }
}
