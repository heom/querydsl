package me.study.jpaquerydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import me.study.jpaquerydsl.dto.MemberDto;
import me.study.jpaquerydsl.dto.QMemberDto;
import me.study.jpaquerydsl.dto.UserDto;
import me.study.jpaquerydsl.entity.Member;
import me.study.jpaquerydsl.entity.QMember;
import me.study.jpaquerydsl.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;
import static me.study.jpaquerydsl.entity.QMember.member;
import static me.study.jpaquerydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL(){
        //member1을 찾아라.
        String qlString = "select m from Member m where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl(){
        //member1을 찾아라.
        Member findMember = queryFactory.select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * @Description [기본문법] Where
     **/
    @Test
    public void search(){
        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * @Description [기본문법] Where - and 쉼표로 가능
     **/
    @Test
    public void searchAndParam(){
        Member findMember = queryFactory.selectFrom(member)
                .where(
                        member.username.eq("member1")
                        , member.age.eq(10)
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * @Description [기본문법] Select
     **/
    @Test
    public void resultFetch(){
        //List
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        //단 건
        Member findMember1 = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        //처음 한 건 조회
        Member findMember2 = queryFactory
                .selectFrom(member)
                .fetchFirst();

        //페이징에서 사용 Deprecated => fetch() 권장
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        //count 쿼리로 변경 Deprecated => 아래 쿼리 권장
        long count = queryFactory
                .selectFrom(member)
                .fetchCount();
        Long totalCount = queryFactory
                //.select(Wildcard.count) //select count(*)
                .select(member.count()) //select count(member.id)
                .from(member)
                .fetchOne();
    }

    /**
     * @Description [기본문법] Sort
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단, 2에서 회원 이름이 없으면 마지막 출력(nulls last)
     **/
    @Test
    public void sort(){
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> list = queryFactory.selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = list.get(0);
        Member member6 = list.get(1);
        Member memberNull = list.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    /**
     * @Description [기본문법] Paging
     **/
    @Test
    public void paging1(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) //0부터 시작(zero index)
                .limit(2) //최대 2건 조회
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }

    /**
     * @Description [기본문법] Paging - Deprecated
     **/
    @Test
    public void paging2() {
        //Deprecated => 아래 쿼리처럼 리스트와 총 갯수 따로 쿼리 작성 필요
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) //0부터 시작(zero index)
                .limit(2) //최대 2건 조회
                .fetch();
        Long totalCount = queryFactory
                .select(member.count()) //select count(member.id)
                .from(member)
                .fetchOne();

        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getResults().size()).isEqualTo(2);

        assertThat(result.size()).isEqualTo(2);
        assertThat(totalCount).isEqualTo(4);
    }

    /**
     * @Description [기본문법] Aggregate
     **/
    @Test
    public void aggregation(){
        List<Tuple> result = queryFactory
                .select(member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /**
     * @Description [기본문법] Aggregate - group by, having
     * 팀의 이름과 각 팀의 평균 연령
     **/
    @Test
    public void group(){
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .having(team.name.startsWith("team"))
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    /**
     * @Description [기본문법] Join(basic)
     * 팀 A에 소속된 모든 회원
     **/
    @Test
    public void join(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    /**
     * @Description [기본문법] Join(basic) - 세타조인(Theta Join)
     * 세타 조인(연관관계가 없는 필드로 조인)
     * 회원의 이름이 팀 이름과 같은 회원 조회
     **/
    @Test
    public void theta_join(){
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    /**
     * @Description [기본문법] Join(on) - 조인 대상 필터링
     * 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and t.name='teamA'
     **/
    @Test
    public void join_on_filtering(){
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                    .on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * @Description [기본문법] Join(on) - 연관관계 없는 entity 외부 조인
     * 회원의 이름과 팀의 이름이 같은 대상 외부 조인
     * JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
     **/
    @Test
    public void join_on_no_relation(){
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team)
                    .on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("t=" + tuple);
        }
    }

    /**
     * @Description [기본문법] Join(fetchJoin) - EntityManager를 만드는 Factory -> PersistenceUnitUtil() 를 사용할 수 있게 해줌
     **/
    @PersistenceUnit
    EntityManagerFactory emf;

    /**
     * @Description [기본문법] Join(fetchJoin) - 페치 조인 미적용
     **/
    @Test
    public void fetchJoinNo(){
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    /**
     * @Description [기본문법] Join(fetchJoin) - 페치 조인 적용
     **/
    @Test
    public void fetchJoinUse(){
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 적용").isTrue();
    }

    /**
     * @Description [기본문법] SubQuery - 서브 쿼리 eq 사용
     * 나이가 가장 많은 회원 조회
     **/
    @Test
    public void subQuery(){
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(40);
    }

    /**
     * @Description [기본문법] SubQuery - 서브 쿼리 goe 사용
     * 나이가 평균 나이 이상인 회원
     **/
    @Test
    public void subQueryGoe(){
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(30,40);
    }

    /**
     * @Description [기본문법] SubQuery - 서브쿼리 여러 건 처리 in 사용
     * 나이가 10 초과인 회원
     **/
    @Test
    public void subQueryIn(){
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(20, 30, 40);
    }

    /**
     * @Description [기본문법] SubQuery - select 절에 subquery
     **/
    @Test
    public void subQuerySelect(){
        QMember memberSub = new QMember("memberSub");

        List<Tuple> fetch = queryFactory
                .select(member.username,
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ).from(member)
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("username = " + tuple.get(member.username));
            System.out.println("age = " +
                    tuple.get(JPAExpressions.select(memberSub.age.avg())
                            .from(memberSub)));
        }
    }

    /**
     * @Description [기본문법] SubQuery - static import 활용
     * import static com.querydsl.jpa.JPAExpressions.select;
     **/
    @Test
    public void subQueryStaticImport(){
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(40);
    }

    /**
     * @Description [기본문법] Case - 단순한 조건
     **/
    @Test
    public void basicCase(){
        List<String> result = queryFactory.select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for(String s : result){
            System.out.println(s);
        }
    }

    /**
     * @Description [기본문법] Case - 복잡한 조건
     **/
    @Test
    public void complexCase(){
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for(String s : result){
            System.out.println(s);
        }
    }

    /**
     * @Description [기본문법] Case - order by, Case 문 함께 사용
     * 0 ~ 30살이 아닌 회원을 가장 먼저 출력
     * 0 ~ 20살 회원 출력
     * 21 ~ 30살 회원 출력
     **/
    @Test
    public void orderByCase(){
        NumberExpression<Integer> rankPath = new CaseBuilder()
                                                            .when(member.age.between(0, 20)).then(2)
                                                            .when(member.age.between(21, 30)).then(1)
                                                            .otherwise(3);

        List<Tuple> result = queryFactory
                    .select(member.username, member.age, rankPath)
                    .from(member)
                    .orderBy(rankPath.desc())
                    .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            Integer rank = tuple.get(rankPath);
            System.out.println("username = " + username + " age = " + age + " rank = "+ rank);
        }
    }

    /**
     * @Description [기본문법] Constant, Concat - Constant
     **/
    @Test
    public void constant(){
        Tuple result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetchFirst();

        System.out.println(result);
    }

    /**
     * @Description [기본문법] Constant, Concat - Concat
     **/
    @Test
    public void concat(){
        String result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        System.out.println(result);
    }

    /**
     * @Description [중급문법] Projection(Result) - 하나의 타입
     **/
    @Test
    public void simpleProjection(){
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        for(String s : result){
            System.out.println(s);
        }
    }

    /**
     * @Description [중급문법] Projection(Result) - 둘 이상의 타입(Tuple)
     **/
    @Test
    public void tupleProjection(){
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for(Tuple t : result){
            String username = t.get(member.username);
            Integer age = t.get(member.age);

            System.out.println("username = "+ username);
            System.out.println("age = "+ age);
        }
    }

    /**
     * @Description [중급문법] Projection(Result) - 둘 이상의 타입(DTO) 변수명 일치 - 순수 JPQL 경우
     * 생성자를 사용해줘야함
     **/
    @Test
    public void findDtoByJPQL(){
        List<MemberDto> resultList = em.createQuery("select new me.study.jpaquerydsl.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();

        for(MemberDto m : resultList){
            System.out.println("memberDto = "+m);
        }
    }

    /**
     * @Description [중급문법] Projection(Result) - 둘 이상의 타입(DTO) 변수명 일치 - Querydsl
     * 프로퍼티 접근 - Setter
     **/
    @Test
    public void findDtoBySetter(){
        List<MemberDto> resultList = queryFactory
                .select(Projections.bean(MemberDto.class
                        , member.username
                        , member.age))
                .from(member)
                .fetch();

        for(MemberDto m : resultList){
            System.out.println("memberDto = "+m);
        }
    }

    /**
     * @Description [중급문법] Projection(Result) - 둘 이상의 타입(DTO) 변수명 일치 - Querydsl
     * 필드 직접 접근
     **/
    @Test
    public void findDtoByField(){
        List<MemberDto> resultList = queryFactory
                .select(Projections.fields(MemberDto.class
                        , member.username
                        , member.age))
                .from(member)
                .fetch();

        for(MemberDto m : resultList){
            System.out.println("memberDto = "+m);
        }
    }

    /**
     * @Description [중급문법] Projection(Result) - 둘 이상의 타입(DTO) 변수명 일치 - Querydsl
     * 생성자 사용
     **/
    @Test
    public void findDtoByConstructor(){
        List<MemberDto> resultList = queryFactory
                .select(Projections.constructor(MemberDto.class
                        , member.username
                        , member.age))
                .from(member)
                .fetch();

        for(MemberDto m : resultList){
            System.out.println("memberDto = "+m);
        }
    }

    /**
     * @Description [중급문법] Projection(Result) - 둘 이상의 타입(DTO) 변수명 불일치(username != name) - Querydsl
     * 별칭 사용
     **/
    @Test
    public void findDtoByAlias(){
        QMember memberSub = new QMember("memberSub");

        List<UserDto> resultList = queryFactory
                .select(Projections.fields(UserDto.class
                                , member.username.as("name")
                                , ExpressionUtils.as(
                                        JPAExpressions
                                                .select(memberSub.age.max())
                                                .from(memberSub), "age")
                        )
                ).from(member)
                .fetch();

        for(UserDto u : resultList){
            System.out.println("userDto = "+u);
        }
    }

    /**
     * @Description [중급문법] Projection(Result) - 둘 이상의 타입(DTO) 변수명 일치 - Querydsl(@QueryProjection)
     * Q Class 생성
     **/
    @Test
    public void findDtoByQueryProjection(){
        List<MemberDto> resultList = queryFactory
                    .select(new QMemberDto(member.username, member.age))
                    .from(member)
                    .fetch();

        for(MemberDto m : resultList){
            System.out.println("memberDto = "+m);
        }
    }

    /**
     * @Description [중급문법] 동적쿼리 - BooleanBuilder
     **/
    @Test
    public void 동적쿼리_BooleanBuilder(){
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);

        assertThat(result.size()).isEqualTo(1);
    }
    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();

        if (usernameCond != null) {
            builder.and(member.username.eq(usernameCond));
        }
        if (ageCond != null) {
            builder.and(member.age.eq(ageCond));
        }

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    /**
     * @Description [중급문법] 동적쿼리 - Where 다중 파라미터 사용
     **/
    @Test
    public void 동적쿼리_WhereParam(){
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember2(usernameParam, ageParam);

        assertThat(result.size()).isEqualTo(1);
    }
    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return queryFactory
                .selectFrom(member)
//                .where(usernameEq(usernameCond), ageEq(ageCond))
                .where(allEq(usernameCond, ageCond))
                .fetch();
    }
    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond != null ? member.username.eq(usernameCond) : null;
    }
    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }
    private BooleanExpression allEq(String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }

    /**
     * @Description [중급문법] Batch Query - update
     **/
    @Test
    public void bulkUpdate(){
        queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        em.flush();
        em.clear();

        List<Member> members = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member member1 : members) {
            System.out.println(member1.getUsername());
        }
    }

    /**
     * @Description [중급문법] Batch Query - update add
     **/
    @Test
    public void bulkAdd(){
        queryFactory
                .update(member)
                .set(member.age, member.age.add(1))
                .execute();

        em.flush();
        em.clear();

        List<Member> members = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member member1 : members) {
            System.out.println(member1);
        }
    }

    /**
     * @Description [중급문법] Batch Query - delete
     **/
    @Test
    public void bulkDelete(){
        queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();

        em.flush();
        em.clear();

        List<Member> members = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member member1 : members) {
            System.out.println(member1);
        }
    }

    /**
     * @Description [중급문법] Sql function - replace 사용
     **/
    @Test
    public void sqlFunction(){
        List<String> result = queryFactory.select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})"
                                                    , member.username
                                                    , "member"
                                                    , "M"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println(s);
        }
    }

    /**
     * @Description [중급문법] Sql function - lower 사용
     **/
    @Test
    public void sqlFunction2(){
        List<String> result = queryFactory.select(member.username)
                .from(member)
//                .where(member.username.eq(Expressions.stringTemplate("function('lower', {0})"
//                                        , member.username)))
                .where(member.username.eq(member.username.lower()))
                .fetch();

        for (String s : result) {
            System.out.println(s);
        }
    }
}
