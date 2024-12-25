package org.sdn.walletservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.json.simple.JSONObject;
import org.sdn.commonservice.commonutilities.Constants;
import org.sdn.walletservice.entity.Wallet;
import org.sdn.walletservice.repository.WalletRepository;
import org.sdn.walletservice.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @Autowired
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository walletRepository;

    @Value("${wallet.initialBalance}")
    private Double initialBalance;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = Constants.USER_CREATION_TOPIC,containerFactory = "userCreationListenerContainerFactory")
    public void walletCreation(String message, Acknowledgment ack) {
        System.out.println("Received Message in group foo: " + message);
        try {
            JSONObject notificationMsgJsonObject = objectMapper.readValue(message, JSONObject.class);
            String userContact =  notificationMsgJsonObject.get(Constants.USER_CONTACT).toString();

            Wallet wallet = Wallet.builder()
                    .balance(initialBalance)
                    .contact(userContact)
                    .build();

            wallet = walletRepository.save(wallet);

            JSONObject walletCreatedTopicData = new JSONObject();
            walletCreatedTopicData.put(Constants.USER_CONTACT, userContact);
            walletCreatedTopicData.put(Constants.USER_EMAIl,notificationMsgJsonObject.get(Constants.USER_EMAIl));
            walletCreatedTopicData.put(Constants.USER_NAME, notificationMsgJsonObject.get(Constants.USER_NAME));
            kafkaTemplate.send(Constants.WALLET_CREATED_TOPIC,objectMapper.writeValueAsString(walletCreatedTopicData));

        }catch (Exception e) {

        }


        ack.acknowledge();
    }

    @KafkaListener(topics = Constants.TXN_SAVED,containerFactory = "txnSavedListenerContainerFactory")
    @Transactional
    public void txnSaved(String message, Acknowledgment ack) throws JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(message,JSONObject.class);
        String senderContact = (String) jsonObject.get("senderContact");
        String receiverContact = (String) jsonObject.get("receiverContact");
        Double amount = (Double) jsonObject.get("amountToTransfer");
//        producerMessage.put("txnId", txn.getTxnId());
//        producerMessage.put("status", txn.getTxnStatus());

        // Update Wallet For the Sender
        walletRepository.updateWallet(senderContact,-amount);

        // Update Wallet For the Receiver
        walletRepository.updateWallet(receiverContact,amount);

        // Send to queue that wallet has been updated

        JSONObject jsonWalletCreatedTopicData = new JSONObject();
        jsonWalletCreatedTopicData.put("message","Wallet Updated successfully");
        jsonWalletCreatedTopicData.put("status","success");
        jsonWalletCreatedTopicData.put("txn_id",jsonObject.get("txnId"));
        kafkaTemplate.send(Constants.WALLET_UPDATED_TOPIC,objectMapper.writeValueAsString(jsonWalletCreatedTopicData));

        ack.acknowledge();
    }
}
