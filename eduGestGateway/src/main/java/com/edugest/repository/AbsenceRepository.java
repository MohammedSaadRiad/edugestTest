package com.edugest.repository;

import com.edugest.domain.Absence;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Absence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AbsenceRepository extends ReactiveCrudRepository<Absence, Long>, AbsenceRepositoryInternal {
    @Query("SELECT * FROM absence entity WHERE entity.student_id = :id")
    Flux<Absence> findByStudent(Long id);

    @Query("SELECT * FROM absence entity WHERE entity.student_id IS NULL")
    Flux<Absence> findAllWhereStudentIsNull();

    @Query("SELECT * FROM absence entity WHERE entity.session_id = :id")
    Flux<Absence> findBySession(Long id);

    @Query("SELECT * FROM absence entity WHERE entity.session_id IS NULL")
    Flux<Absence> findAllWhereSessionIsNull();

    @Override
    <S extends Absence> Mono<S> save(S entity);

    @Override
    Flux<Absence> findAll();

    @Override
    Mono<Absence> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AbsenceRepositoryInternal {
    <S extends Absence> Mono<S> save(S entity);

    Flux<Absence> findAllBy(Pageable pageable);

    Flux<Absence> findAll();

    Mono<Absence> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Absence> findAllBy(Pageable pageable, Criteria criteria);
}
