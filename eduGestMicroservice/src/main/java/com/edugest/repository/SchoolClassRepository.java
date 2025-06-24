package com.edugest.repository;

import com.edugest.domain.SchoolClass;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the SchoolClass entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SchoolClassRepository extends ReactiveCrudRepository<SchoolClass, Long>, SchoolClassRepositoryInternal {
    @Override
    Mono<SchoolClass> findOneWithEagerRelationships(Long id);

    @Override
    Flux<SchoolClass> findAllWithEagerRelationships();

    @Override
    Flux<SchoolClass> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM school_class entity WHERE entity.timetable_id = :id")
    Flux<SchoolClass> findByTimetable(Long id);

    @Query("SELECT * FROM school_class entity WHERE entity.timetable_id IS NULL")
    Flux<SchoolClass> findAllWhereTimetableIsNull();

    @Query(
        "SELECT entity.* FROM school_class entity JOIN rel_school_class__teachers joinTable ON entity.id = joinTable.teachers_id WHERE joinTable.teachers_id = :id"
    )
    Flux<SchoolClass> findByTeachers(Long id);

    @Override
    <S extends SchoolClass> Mono<S> save(S entity);

    @Override
    Flux<SchoolClass> findAll();

    @Override
    Mono<SchoolClass> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SchoolClassRepositoryInternal {
    <S extends SchoolClass> Mono<S> save(S entity);

    Flux<SchoolClass> findAllBy(Pageable pageable);

    Flux<SchoolClass> findAll();

    Mono<SchoolClass> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<SchoolClass> findAllBy(Pageable pageable, Criteria criteria);

    Mono<SchoolClass> findOneWithEagerRelationships(Long id);

    Flux<SchoolClass> findAllWithEagerRelationships();

    Flux<SchoolClass> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
