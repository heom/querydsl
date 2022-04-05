package me.study.jpaquerydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

/**
 * @Description [중급문법] Projection(Result) - 둘 이상의 타입(DTO)로 반환하는 경우, 변수명 일치
 **/
@Data
public class MemberDto {

    private String username;
    private int age;

    /**
     * @Description [중급문법] Projection(Result) - 둘 이상의 타입(DTO) - Querydsl
     * 프로퍼티 접근 - Setter 사용시 필수!! 기본 빈 생성자
     **/
    public MemberDto() {
    }

    /**
     * @Description [중급문법] Projection(Result) - 둘 이상의 타입(DTO)
     * @QueryProjection 사용 경우
     **/
    @QueryProjection
    /**
     * @Description [중급문법] Projection(Result) - 둘 이상의 타입(DTO)
     * 순수 JPQL 경우 필수!! 기본 필요 파라미터 생성자
     * Querydsl 생성자 사용 경우 필수!! 단, 타입 매칭되야함
     **/
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
