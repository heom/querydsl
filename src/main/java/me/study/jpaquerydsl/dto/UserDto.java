package me.study.jpaquerydsl.dto;

import lombok.Data;

/**
 * @Description [Projection(Result)] 둘 이상의 타입(DTO)로 반환하는 경우, 변수명 불일치(username != name)
 **/
@Data
public class UserDto {
    private String name;
    private int age;
}
