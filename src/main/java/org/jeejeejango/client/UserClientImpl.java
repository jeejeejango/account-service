package org.jeejeejango.client;

import lombok.extern.slf4j.Slf4j;
import org.jeejeejango.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jeejeejango
 * @since 22/01/2019 11:18
 */
@Service
@Slf4j
public class UserClientImpl implements UserClient {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${client.user.protocol:http}")
    private String protocol;

    @Value("${client.user.host:localhost}")
    private String host;

    @Value("${client.user.port}")
    private Integer port;

    @Value("${client.user.endpoint}")
    private String endpoint;

    private String userURL;


    @PostConstruct
    public void init() {
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("admin", "password1"));
        userURL = protocol + "://" + host + ":" + port + endpoint;
        if (log.isInfoEnabled()) {
            log.info("user url {}", userURL);
        }
    }


    public User getUserById(Long id) {
        try {
            return restTemplate.getForObject(userURL + "/{id}", User.class, id);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw e;
            }
            return null;
        }
    }


    public void validateUser(Long id) {
        if (id != null && getUserById(id) == null) {
            if (log.isErrorEnabled()) {
                log.error("User id {} is invalid", id);
            }
            throw new NoSuchUserException("User is invalid");
        }
    }


    @Override
    public List<Long> searchUsers(String firstName, String lastName, String email) {
        ResponseEntity<List<User>> response = restTemplate.exchange(
            userURL + "/search?firstName={firstName}&lastName={lastName}&email={email}&unpaged=true",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<User>>() {
            },
            firstName, lastName, email);
        List<User> users = response.getBody();
        if (users != null && !users.isEmpty()) {
            final List<Long> results = new ArrayList<>();
            users.forEach(user -> results.add(user.getId()));
            return results;
        }
        return Collections.emptyList();
    }


}
