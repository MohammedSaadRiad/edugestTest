package com.edugest.repository;

import com.edugest.domain.Teacher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Teacher entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeacherRepository extends ReactiveCrudRepository<Teacher, Long>, TeacherRepositoryInternal {
    @Override
    Mono<Teacher> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Teacher> findAllWithEagerRelationships();

    @Override
    Flux<Teacher> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM teacher entity JOIN rel_teacher__subjects joinTable ON entity.id = joinTable.subjects_id WHERE joinTable.subjects_id = :id"
    )
    Flux<Teacher> findBySubjects(Long id);

    @Override
    <S extends Teacher> Mono<S> save(S entity);

    @Override
    Flux<Teacher> findAll();

    @Override
    Mono<Teacher> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TeacherRepositoryInternal {
    <S extends Teacher> Mono<S> save(S entity);

    Flux<Teacher> findAllBy(Pageable pageable);

    Flux<Teacher> findAll();

    Mono<Teacher> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Teacher> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Teacher> findOneWithEagerRelationships(Long id);

    Flux<Teacher> findAllWithEagerRelationships();

    Flux<Teacher> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
