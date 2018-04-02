package com.microservices.support.domain;


import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "permission_ids")
    private String permissionIds;

    private String type;

    @Column(name = "school_id")
    private Long schoolId;

    private String description;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    private String name;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "create_time")
    private Date createTime;

}
