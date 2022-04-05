package me.study.jpaquerydsl.repository;

import me.study.jpaquerydsl.dto.MemberSearchCondition;
import me.study.jpaquerydsl.dto.MemberTeamDto;

import java.util.List;

/**
 * @Description [Spring Data JPA / Querydsl] - 동적쿼리 커스텀 repository
 **/
public interface MemberRepositoryCustom {
    List<MemberTeamDto> searchByWhere(MemberSearchCondition condition);
}
