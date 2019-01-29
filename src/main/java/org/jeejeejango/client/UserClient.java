package org.jeejeejango.client;


import org.jeejeejango.entity.User;

import java.util.List;

/**
 * @author jeejeejango
 * @since 22/01/2019 10:35
 */
public interface UserClient {

    User getUserById(Long id);


    void validateUser(Long id);


    List<Long> searchUsers(String firstName, String lastName, String email);

}
