package com.wangyang.pojo.entity;

import com.wangyang.common.pojo.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "options")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Option extends BaseEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
    /**
     * option key
     */
    @Column(name = "option_key", columnDefinition = "varchar(100) not null")
    private String key;
    /**
     * option value
     */
    @Column(name = "option_value", columnDefinition = "varchar(1023) not null")
    private String value;

    private String name;
    private Integer groupId;





}
