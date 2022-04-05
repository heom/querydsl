package me.study.jpaquerydsl.dto;

import lombok.Data;

/**
 * @Description [순수 JPA / Querydsl] 동적쿼리 Builder - 검색조건
 **/
@Data
public class MemberSearchCondition {

    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}
