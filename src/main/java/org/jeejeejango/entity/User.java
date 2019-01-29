package org.jeejeejango.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jeejeejango
 * @since 18/11/2018 7:05 PM
 */
@Data
public class User implements Serializable {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;


}
