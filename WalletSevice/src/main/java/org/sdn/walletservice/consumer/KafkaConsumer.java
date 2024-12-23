package org.sdn.walletservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @KafkaListener(topics = Constants.USER_CREATION_TOPIC)
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
}
