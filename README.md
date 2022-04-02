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
  
     
