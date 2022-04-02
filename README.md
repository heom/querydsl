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
  - SPRING-DATA-JPA만 사용할 경우, 복잡한 쿼리와 동적쿼리 사용이 어려움.  
    따라서, 상기 문제를 Querydsl 추가로 사용하여 해결함.
------------
- **Querydsl Setting**
  - **[참조]** 강의 QueryDSL 설정과 검증
  - **[중요]** 스프링 부트 2.6 이상, Querydsl 5.0 부터 설정 방법이 약간 변경됨
  - **[참조]** build.gradle 
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