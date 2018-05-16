package com.oauth.services.domain;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_audit")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserAudit {
    @Id
    @Column(name = "id")
//    @GeneratedValue(strategy=GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "action_time")
    private Timestamp actionTime;

    @Column(name = "action_type")
    private String actionType;
}
