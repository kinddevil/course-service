package com.microservices.support.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "user_action")
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "name")
    private String name;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "school_id")
    private Long schoolID;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "action_time")
    private Timestamp actionTime;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "api_name")
    private String apiName;

    @Column(name = "api_method")
    private String apiMethod;

    @Column(name = "content")
    private String content;

    @Column(name = "message")
    private String message;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "action_name")
    private String actionName;
}
