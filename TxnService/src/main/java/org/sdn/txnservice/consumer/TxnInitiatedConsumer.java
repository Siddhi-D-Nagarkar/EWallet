package org.sdn.txnservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.sdn.commonservice.commonutilities.Constants;
import org.sdn.txnservice.dto.kafkaDTO.SenderReceiverInfo;
import org.sdn.txnservice.entity.Txn;
import org.sdn.txnservice.entity.TxnStatus;
import org.sdn.txnservice.repository.TxnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TxnInitiatedConsumer {

    @Autowired
    private TxnRepository txnRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = Constants.TXN_INITIATED_TOPIC,
            groupId = "txnInitGroup",
            containerFactory = "txnFactory")
    public void listenTxnInitiated(SenderReceiverInfo message, Acknowledgment ack) throws JsonProcessingException {
        Txn txn = Txn.builder()
                .txnId(UUID.randomUUID().toString())
                .senderMessage(message.getSenderContact())
                .receiverContactNo(message.getReceiverContact())
                .senderContactNo(message.getSenderContact())
                .amount(message.getAmount())
                .txnStatus(TxnStatus.INITIATED)
                .build();

        txnRepository.save(txn);

        //SEND KAFKA
        JSONObject producerMessage = new JSONObject();
        producerMessage.put("senderContact", txn.getSenderContactNo());
        producerMessage.put("receiverContact", txn.getReceiverContactNo());
        producerMessage.put("amountToTransfer", txn.getAmount());
        producerMessage.put("txnId", txn.getTxnId());
        producerMessage.put("message", txn.getSenderMessage());
        producerMessage.put("status", txn.getTxnStatus());
        kafkaTemplate.send(Constants.TXN_SAVED,objectMapper.writeValueAsString(producerMessage));

        System.out.println("Received Message in group txnInitGroup: " + message);

        ack.acknowledge();
    }

}
