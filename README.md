# Querydsl
- SPRING-DATA-JPA 와 Querydsl 공부하기 

## 프로젝트 개발 구성
- Java 8
- Spring Boot(+Gradle) 2.6.6
- H2
- SpringDataJPA
- JUnit5
- Querydsl

## 추가 정리
- **Querydsl?**
  - SPRING-DATA-JPA만 사용할 경우, 복잡한 쿼리와 동적쿼리 사용이 어려움  
    따라서, 상기 문제를 Querydsl 추가로 사용하여 해결함
------------
- **Querydsl Setting**
  - **[참조]** 강의 QueryDSL 설정과 검증
  - **[중요]** 스프링 부트 2.6 이상, Querydsl 5.0 부터 설정 방법이 약간 변경됨
  - **[참조]** [build.gradle](build.gradle) 
  - 우측 Gradle -> Tasks -> other -> compileQuerydsl 실행
  - 설정 위치에 QClass 생성 확인
     - **[중요]** @Entity가 포함된 Class들만 QClass가 생성됨
------------
- **JPQL vs Querydsl**
  - JPQL은 Query를 String 형태로 만들기 때문에, runtime error 발생
  - Querydsl은 객체지향 자바로 만들기 때문에, compile error 발생
------------
- **Where**
  - 기본
    - ex)
      - member.username.eq("member1") // username = 'member1'
      - member.username.ne("member1") //username != 'member1'
      - member.username.eq("member1").not() // username != 'member1'
      - member.username.isNotNull() //이름이 is not null
      - member.age.in(10, 20) // age in (10,20)
      - member.age.notIn(10, 20) // age not in (10, 20)
      - member.age.between(10,30) //between 10, 30
      - member.age.goe(30) // age >= 30
      - member.age.gt(30) // age > 30
      - member.age.loe(30) // age <= 30
      - member.age.lt(30) // age < 30
      - member.username.like("member%") //like 검색
      - member.username.contains("member") // like ‘%member%’ 검색
      - member.username.startsWith("member") //like ‘member%’ 검색
  - and 조건은 쉼표로 가능 
    - ex)
      - Member findMember = queryFactory.selectFrom(member)  
                .where(member.username.eq("member1")  
                        .and(member.age.eq(10)))  
                .fetchOne();
     
      - Member findMember = queryFactory.selectFrom(member)  
                .where(  
                        member.username.eq("member1")  
                        , member.age.eq(10)  
                )  
                .fetchOne();
------------
- **Select**
  - fetch() : 리스트 조회, 데이터 없으면 빈 리스트 반환
  - fetchOne() : 단 건 조회
    - 결과가 없으면 : null
    - 결과가 둘 이상이면 : com.querydsl.core.NonUniqueResultException
  - fetchFirst() : limit(1).fetchOne()
  - fetchResults() : 페이징 정보 포함, total count 쿼리 추가 실행
    - **[중요]** Deprecated(향후 미지원) - fetch() 권장
  - fetchCount() : count 쿼리로 변경해서 count 수 조회
    - **[중요]** Deprecated(향후 미지원) - fetchOne() 권장
    - ex)
      - Long totalCount = queryFactory  
        //.select(Wildcard.count) //select count(*)  
        .select(member.count()) //select count(member.id)  
        .from(member)  
        .fetchOne();  
------------
- **Sort**
  - desc() , asc() : 일반 정렬
  - nullsLast() , nullsFirst() : null 데이터 순서 부여
------------
- **Paging**
  - **[중요]** 기존 사용하던 fetchResults() Deprecated, 리스트와 총 갯수 따로 쿼리 작성 필요
------------
- **Aggregate**
  - **[중요]** List<Tuple> 객체로 해당 값 가져옴, 하지만 DTO 가져오는 방법을 실무에서 더 많이 사용
    - ex)
      - COUNT(m), //회원수
      - SUM(m.age), //나이 합
      - AVG(m.age), //평균 나이
      - MAX(m.age), //최대 나이
      - MIN(m.age) //최소 나이
  - group by, having 
    - **[참조]** [QuerydslBasicTest.class](src/test/java/me/study/jpaquerydsl/QuerydslBasicTest.java)
------------
- **Join[basic]**
  - **[중요]** 조인의 기본 문법은 첫 번째 파라미터에 조인 대상을 지정하고, 두 번째 파라미터에 별칭(alias)으로 사용할 Q 타입을 지정하면 된다
    - ex)
      - join(조인 대상, 별칭으로 사용할 Q타입)
  - join() , innerJoin() : 내부 조인(inner join)
  - leftJoin() : left 외부 조인(left outer join)
  - rightJoin() : right 외부 조인(right outer join)
  - JPQL의 on 과 성능 최적화를 위한 fetch 조인 제공
  - **[중요]** 세타조인(Theta Join) - JPA 특성상 @Entity 내 연관관계가 없으면 조인이 안될 거 같지만 가능!!
    - from 절에 여러 엔티티를 선택해서 세타 조인
    - **[중요]** 외부 조인 불가능
    - **[참조]** [QuerydslBasicTest.class](src/test/java/me/study/jpaquerydsl/QuerydslBasicTest.java)
------------
- **Join[on]**
  - on절을 활용한 조인(JPA 2.1부터 지원)
  - 조인 대상 필터링
    - on 절을 활용해 조인 대상을 필터링 할 때, 외부조인이 아니라 내부조인(inner join)을 사용하면,  
    where 절에서 필터링 하는 것과 기능이 동일하다. 따라서 on 절을 활용한 조인 대상 필터링을 사용할 때,  
    내부조인 이면 익숙한 where 절로 해결하고, 정말 외부조인이 필요한 경우에만 이 기능을 사용하자.  
  - 연관관계 없는 entity 외부 조인
    - 하이버네이트 5.1부터 on 을 사용해서 서로 관계가 없는 필드로 외부 조인하는 기능이 추가되었다.  
      물론 내부 조인도 가능하다.
    - **[중요]** 주의! 문법을 잘 봐야 한다. leftJoin() 부분에 일반 조인과 다르게 엔티티 하나만 들어간다.
      - 일반조인: leftJoin(member.team, team) <- entity 내 fk 매칭 추가o
      - on조인: from(member).leftJoin(team).on(xxx) <- entity 내 fk 매칭 추가x, 오직 on 절만
  - **[참조]** [QuerydslBasicTest.class](src/test/java/me/study/jpaquerydsl/QuerydslBasicTest.java)
------------
- **Join[fetchJoin]**
  - 페치 조인은 SQL에서 제공하는 기능은 아니다. SQL조인을 활용해서 연관된 엔티티를 SQL 한번에 조회하는 기능이다.   
    주로 성능 최적화에 사용하는 방법이다.
  - fetch = FetchType.LAZY - 지연로딩할 때, 데이터 한번에 들고 오는 방법
  - join(), leftJoin() 등 조인 기능 뒤에 fetchJoin() 이라고 추가하면 된다.
  - **[참조]** [QuerydslBasicTest.class](src/test/java/me/study/jpaquerydsl/QuerydslBasicTest.java)
------------
- **SubQuery**
  - com.querydsl.jpa.JPAExpressions 사용
  - **[중요]** from 절의 서브쿼리 한계
    - JPA JPQL 서브쿼리의 한계점으로 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다.   
      당연히 Querydsl도 지원하지 않는다. 하이버네이트 구현체를 사용하면 select 절의 서브쿼리는 지원한다.   
      Querydsl도 하이버네이트 구현체를 사용하면 select 절의 서브쿼리를 지원한다.
    - from 절의 서브쿼리 해결방안
      - 서브쿼리를 join으로 변경한다. (가능한 상황도 있고, 불가능한 상황도 있다.)
      - 애플리케이션에서 쿼리를 2번 분리해서 실행한다.
      - nativeSQL을 사용한다.
  - **[참조]** [QuerydslBasicTest.class](src/test/java/me/study/jpaquerydsl/QuerydslBasicTest.java)
------------
- **Case**
  - select, 조건절(where), order by에서 사용 가능
  - **[참조]** [QuerydslBasicTest.class](src/test/java/me/study/jpaquerydsl/QuerydslBasicTest.java)
------------
- **Constant, Concat**
  - Constant 
    - 상수가 필요하면 Expressions.constant(xxx) 사용
    - 최적화가 가능하면 SQL에 constant 값을 넘기지 않는다. 상수를 더하는 것 처럼 최적화가 어려우면 SQL에 constant 값을 넘긴다.
  - Concat 
    - member.age.stringValue() 부분이 중요한데, 문자가 아닌 다른 타입들은 stringValue()로 문자로 변환할 수 있다. 이 방법은 ENUM을 처리할 때도 자주 사용한다.
  - **[참조]** [QuerydslBasicTest.class](src/test/java/me/study/jpaquerydsl/QuerydslBasicTest.java)
------------
- **Projection(Result)**
  - Projection 
    - select 대상 지정
    - Projection 대상이 하나면 타입을 명확하게 지정할 수 있음 ex) String
    - Projection 대상이 둘 이상이면 Tuple이나 DTO로 조회 
    - **[중요]** DTO로 조회
      - 순수 JPA
        - 순수 JPA에서 DTO를 조회할 때는 new 명령어를 사용해야함
        - DTO의 package이름을 다 적어줘야해서 지저분함
        - 생성자 방식만 지원함
      - Querydsl 빈 생성(Bean population)
        - 다음 4가지 방법 지원
          - 프로퍼티 접근 (MemberDto)
          - 필드 직접 접근 (MemberDto)
          - 생성자 사용 (MemberDto)
          - 변수 명칭이 다를 경우 (UserDto)
            - ExpressionUtils.as(source,alias) : 필드나, 서브 쿼리에 별칭 적용
            - username.as("memberName") : 필드에 별칭 적용
      - Querydsl DTO Q Class 생성
        - 기본 필요 파라미터 생성자위에 @QueryProjection 사용
        - 이 방법은 컴파일러로 타입을 체크할 수 있으므로 가장 안전한 방법이다.   
          다만 DTO에 QueryDSL 어노테이션을 유지해야 하는 점과 DTO까지 Q 파일을 생성해야 하는 단점이 있다.
  - **[참조]** [QuerydslBasicTest.class](src/test/java/me/study/jpaquerydsl/QuerydslBasicTest.java)
------------
- **동적쿼리**
  - BooleanBuilder
  - Where 다중 파라미터 사용
    - where 조건에 null 값은 무시된다.
    - 메서드를 다른 쿼리에서도 재활용 할 수 있다.
    - 쿼리 자체의 가독성이 높아진다.
    - **[중요]** 여러개 조합도 가능함 
      - null 체크는 주의해서 처리해야함
  - **[참조]** [QuerydslBasicTest.class](src/test/java/me/study/jpaquerydsl/QuerydslBasicTest.java)
------------
- **Batch Query**
  - **[중요]** JPQL 배치와 마찬가지로, 영속성 컨텍스트에 있는 엔티티를 무시하고 실행되기 때문에 배치 쿼리를
      실행하고 나면 영속성 컨텍스트를 초기화 하는 것이 안전하다.
  - **[참조]** [QuerydslBasicTest.class](src/test/java/me/study/jpaquerydsl/QuerydslBasicTest.java)
------------
- **Sql function**
  - SQL function은 JPA와 같이 Dialect에 등록된 내용만 호출할 수 있다.
  - **[참조]** [QuerydslBasicTest.class](src/test/java/me/study/jpaquerydsl/QuerydslBasicTest.java)