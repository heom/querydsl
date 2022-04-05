package me.study.jpaquerydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@SpringBootApplication
public class JpaQuerydslApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaQuerydslApplication.class, args);
    }

    /**
     * @Description [순수 JPA / Querydsl] jpaQueryFactory 빈등록
     **/
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}
