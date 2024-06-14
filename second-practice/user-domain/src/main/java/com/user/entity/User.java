package com.user.entity;

import com.user.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    private Long id;
    private String email;
    private String password;
    private String phoneNum;
    private String name;
    private String grade;
    private int totalOrderCount;

    @Builder
    public User(Long id, String email, String password, String phoneNum, String name, String grade, int totalOrderCount) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
        this.name = name;
        this.grade = grade;
        this.totalOrderCount = totalOrderCount;
    }
}
