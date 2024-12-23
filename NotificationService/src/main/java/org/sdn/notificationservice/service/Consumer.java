package org.sdn.notificationservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.sdn.commonservice.commonutilities.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

    @Autowired
    private SimpleMailMessage simpleMailMessage;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);
    @KafkaListener(topics = "JBDL76USER_CREATE", groupId = "notification-service")
    public void listen(String message)  {
        try {
            JSONObject object = objectMapper.readValue(message, JSONObject.class);
            String name = (String) object.get(Constants.USER_NAME);
            String email = (String) object.get(Constants.USER_EMAIl);
            sendEmail(email,"EWallet User Created !!","Welcome "+ name+ " to Ewallet. User has been Created, wallet will created in while.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @KafkaListener(topics = Constants.WALLET_CREATED_TOPIC,groupId = "${notification.consumer.groupid}")
    public void walletCreateTopicListener(String walletCreatedTopicMessage) throws JsonProcessingException {
        logger.info("Listener of Wallet Created listens this :- "+walletCreatedTopicMessage);
        System.out.println("Listener of Wallet Created listens this :- "+walletCreatedTopicMessage);
        JSONObject object = objectMapper.readValue(walletCreatedTopicMessage, JSONObject.class);
        String name = (String) object.get(Constants.USER_NAME);
        String email = (String) object.get(Constants.USER_EMAIl);
        // Send Email
        sendEmail(email,"Wallet Created ","Welcome "+ name+ " to Ewallet. Wallet has been created ");
    }

    private void sendEmail(String receiverEmail, String subject, String body) throws JsonProcessingException {
        simpleMailMessage.setTo(receiverEmail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setFrom("ewallet@jbdl-76.com");
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
    }
}
