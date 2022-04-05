package me.study.jpaquerydsl.repository;

import me.study.jpaquerydsl.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Description [Spring Data JPA / Querydsl]
 **/
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    List<Member> findByUsername(String username);
}
