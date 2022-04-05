package me.study.jpaquerydsl.repository;

import me.study.jpaquerydsl.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

/**
 * @Description [Spring Data JPA / Querydsl]
 **/
public interface MemberRepository extends JpaRepository<Member, Long> // 기본
        , MemberRepositoryCustom // 커스텀 [Spring Data JPA / Querydsl]
        , QuerydslPredicateExecutor // [Spring Data JPA에서 제공하는 Querydsl 기능] QuerydslPredicateExecutor 인터페이스
        {
    List<Member> findByUsername(String username);
}
