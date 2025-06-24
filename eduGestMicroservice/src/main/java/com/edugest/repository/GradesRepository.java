package com.edugest.repository;

import com.edugest.domain.Grades;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Grades entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GradesRepository extends ReactiveCrudRepository<Grades, Long>, GradesRepositoryInternal {
    @Query("SELECT * FROM grades entity WHERE entity.student_id = :id")
    Flux<Grades> findByStudent(Long id);

    @Query("SELECT * FROM grades entity WHERE entity.student_id IS NULL")
    Flux<Grades> findAllWhereStudentIsNull();

    @Query("SELECT * FROM grades entity WHERE entity.subject_id = :id")
    Flux<Grades> findBySubject(Long id);

    @Query("SELECT * FROM grades entity WHERE entity.subject_id IS NULL")
    Flux<Grades> findAllWhereSubjectIsNull();

    @Override
    <S extends Grades> Mono<S> save(S entity);

    @Override
    Flux<Grades> findAll();

    @Override
    Mono<Grades> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface GradesRepositoryInternal {
    <S extends Grades> Mono<S> save(S entity);

    Flux<Grades> findAllBy(Pageable pageable);

    Flux<Grades> findAll();

    Mono<Grades> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Grades> findAllBy(Pageable pageable, Criteria criteria);
}
