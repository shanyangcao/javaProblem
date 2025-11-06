package com.shuxuejia.managementsystem.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
   @auth0r  chagumu
    
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private Integer id;
    private String lastname;
    private String email;
    private Integer gender;
    private Department department;
    private Date birth;
}
