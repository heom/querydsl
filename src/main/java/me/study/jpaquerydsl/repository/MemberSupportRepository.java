package me.study.jpaquerydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import me.study.jpaquerydsl.dto.MemberSearchCondition;
import me.study.jpaquerydsl.dto.MemberTeamDto;
import me.study.jpaquerydsl.dto.QMemberTeamDto;
import me.study.jpaquerydsl.entity.Member;
import me.study.jpaquerydsl.repository.support.CustomQuerydslRepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.core.types.ExpressionUtils.count;
import static me.study.jpaquerydsl.entity.QMember.member;
import static me.study.jpaquerydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

/**
 * @Description [Spring Data JPA에서 제공하는 Querydsl 기능] QuerydslRepositorySupport
 *                                                          - QuerydslRepositorySupport 직접 만들기 사용
 **/
@Repository
public class MemberSupportRepository extends CustomQuerydslRepositorySupport {

    public MemberSupportRepository() {
        super(Member.class);
    }

    public List<Member> basicSelect() {
        return select(member)
                .from(member)
                .fetch();
    }
    public List<Member> basicSelectFrom() {
        return selectFrom(member)
                .fetch();
    }
    public Page<MemberTeamDto> searchPageByApplyPage(MemberSearchCondition condition, Pageable pageable) {
        JPAQuery<MemberTeamDto> contentQuery =  select(new QMemberTeamDto(
                                                member.id.as("memberId")
                                                , member.username
                                                , member.age
                                                , team.id.as("teamId")
                                                , team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername())
                        , teamNameEq(condition.getTeamName())
                        , ageGoe(condition.getAgeGoe())
                        , ageLoe(condition.getAgeLoe()));

        List<MemberTeamDto> content = getQuerydsl().applyPagination(pageable, contentQuery).fetch();

        JPAQuery<Long> countQuery = select(count(member.id))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername())
                        , teamNameEq(condition.getTeamName())
                        , ageGoe(condition.getAgeGoe())
                        , ageLoe(condition.getAgeLoe())
                );

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    public Page<MemberTeamDto> applyPagination(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable
                        , contentQuery -> contentQuery
                                                        .select(new QMemberTeamDto(
                                                                    member.id.as("memberId")
                                                                    , member.username
                                                                    , member.age
                                                                    , team.id.as("teamId")
                                                                    , team.name.as("teamName")))
                                                        .from(member)
                                                        .leftJoin(member.team, team)
                                                        .where(usernameEq(condition.getUsername())
                                                                , teamNameEq(condition.getTeamName())
                                                                , ageGoe(condition.getAgeGoe())
                                                                , ageLoe(condition.getAgeLoe()))
                        , countQuery -> countQuery
                                                        .select(count(member.id))
                                                        .from(member)
                                                        .leftJoin(member.team, team)
                                                        .where(usernameEq(condition.getUsername())
                                                                , teamNameEq(condition.getTeamName())
                                                                , ageGoe(condition.getAgeGoe())
                                                                , ageLoe(condition.getAgeLoe()))
        );
    }

    private BooleanExpression usernameEq(String username) {
        return hasText(username)?member.username.eq(username):null;
    }
    private BooleanExpression teamNameEq(String teamName) {
        return hasText(teamName)?team.name.eq(teamName):null;
    }
    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null?member.age.goe(ageGoe):null;
    }
    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null?member.age.loe(ageLoe):null;
    }
}
