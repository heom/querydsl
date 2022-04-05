package me.study.jpaquerydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import me.study.jpaquerydsl.dto.MemberSearchCondition;
import me.study.jpaquerydsl.dto.MemberTeamDto;
import me.study.jpaquerydsl.dto.QMemberTeamDto;

import javax.persistence.EntityManager;
import java.util.List;

import static me.study.jpaquerydsl.entity.QMember.member;
import static me.study.jpaquerydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

/**
 * @Description [Spring Data JPA / Querydsl] - 동적쿼리 커스텀 repository
 **/
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MemberTeamDto> searchByWhere(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId")
                        , member.username
                        , member.age
                        , team.id.as("teamId")
                        , team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername())
                        , teamNameEq(condition.getTeamName())
                        , ageGoe(condition.getAgeGoe())
                        , ageLoe(condition.getAgeLoe())
                )
                .fetch();
    }

    private BooleanExpression usernameEq(String username) {return hasText(username)?member.username.eq(username):null;}
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
