package org.sdn.notificationservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.sdn.notificationservice.utility.Constants;
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

            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("EWallet User Created !!");
            simpleMailMessage.setFrom("ewallet@jbdl-76.com");
            simpleMailMessage.setText("Welcome "+ name+ " to Ewallet. User has been Created, wallet will created in while.");
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
