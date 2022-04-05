package me.study.jpaquerydsl.repository;

import me.study.jpaquerydsl.dto.MemberSearchCondition;
import me.study.jpaquerydsl.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Description [Spring Data JPA / Querydsl] - 커스텀 repository
 **/
public interface MemberRepositoryCustom {
    /**
     * @Description [Spring Data JPA / Querydsl] Repository
     **/
    List<MemberTeamDto> searchByWhere(MemberSearchCondition condition);

    /**
     * @Description [Spring Data JPA / Querydsl] Paging
     **/
    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable); // Deprecated
    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
}
