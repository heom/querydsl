package me.study.jpaquerydsl.controller;

import lombok.RequiredArgsConstructor;
import me.study.jpaquerydsl.dto.MemberSearchCondition;
import me.study.jpaquerydsl.dto.MemberTeamDto;
import me.study.jpaquerydsl.entity.Member;
import me.study.jpaquerydsl.repository.MemberJpaRepository;
import me.study.jpaquerydsl.repository.MemberRepository;
import me.study.jpaquerydsl.repository.MemberSupportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;
    private final MemberSupportRepository memberSupportRepository;

    /**
     * @Description [순수 JPA / Querydsl] API TEST
     **/
    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition){
        return memberJpaRepository.searchByWhere(condition);
    }

    /**
     * @Description [Spring Data JPA / Querydsl] API TEST
     **/
    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchPageSimple(condition, pageable);
    }
    @GetMapping("/v3/members")
    public Page<MemberTeamDto> searchMemberV3(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchPageComplex(condition, pageable);
    }

    /**
     * @Description [Spring Data JPA에서 제공하는 Querydsl 기능] QuerydslRepositorySupport
     **/
    @GetMapping("/v4/members")
    public Page<MemberTeamDto> searchMemberV4(MemberSearchCondition condition, Pageable pageable) {
        return memberSupportRepository.searchPageByApplyPage(condition, pageable);
    }
    @GetMapping("/v5/members")
    public Page<MemberTeamDto> searchMemberV5(MemberSearchCondition condition, Pageable pageable) {
        return memberSupportRepository.applyPagination(condition, pageable);
    }
}
