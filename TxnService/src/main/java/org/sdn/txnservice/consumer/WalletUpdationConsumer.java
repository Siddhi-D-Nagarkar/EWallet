package org.sdn.txnservice.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.sdn.commonservice.commonutilities.Constants;
import org.sdn.txnservice.repository.TxnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;


@Service
public class WalletUpdationConsumer {

    @Autowired
    private TxnRepository txnRepo ;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = Constants.WALLET_UPDATED_TOPIC,containerFactory = "stringFactory")
    public void listenWalletUpdation(String message, Acknowledgment ack) throws JsonProcessingException {
        JSONObject jsonObject = objectMapper.readValue(message, JSONObject.class);
        String txnId = jsonObject.get("txn_id").toString();
        String status = jsonObject.get("status").toString();
        String msg = jsonObject.get("message").toString();

        //



    }
}
