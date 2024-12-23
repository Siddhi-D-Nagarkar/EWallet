package org.sdn.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.sdn.commonservice.commonutilities.Constants;
import org.sdn.userservice.dto.request.UserRequestDTO;
import org.sdn.userservice.entity.User;
import org.sdn.userservice.entity.UserType;
import org.sdn.userservice.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${user.Authority}")
    private String userAuthority;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public User addUpdate(UserRequestDTO userRequestDTO) throws JsonProcessingException {
        User user = userRequestDTO.toUser();
        user.setAuthorities(userAuthority);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserType(UserType.CUSTOMER);
        User userFromDB = userRepository.findByEmail(userRequestDTO.getEmail());
        JSONObject jsonObject = new JSONObject();
        if (userFromDB != null) {
            jsonObject.put(Constants.NEW_USER, true);
        }

        jsonObject.put(Constants.USER_CONTACT, user.getPhoneNo());
        jsonObject.put(Constants.USER_EMAIl, user.getEmail());
        jsonObject.put(Constants.USER_IDENTIFIER, user.getIdentifier());
        jsonObject.put(Constants.USER_IDENTIFIER_VALUE, user.getUserIdentifierValue());
        jsonObject.put(Constants.USER_NAME, user.getName());
        jsonObject.put(Constants.USER_ID, user.getId());
        kafkaTemplate.send(Constants.USER_ALTERATION_TOPIC, objectMapper.writeValueAsString(jsonObject));
        return userRepository.save(user);

    }
}
