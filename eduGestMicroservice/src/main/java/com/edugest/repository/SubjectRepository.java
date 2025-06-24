package com.edugest.repository;

import com.edugest.domain.Subject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Subject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubjectRepository extends ReactiveCrudRepository<Subject, Long>, SubjectRepositoryInternal {
    @Override
    Mono<Subject> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Subject> findAllWithEagerRelationships();

    @Override
    Flux<Subject> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM subject entity JOIN rel_subject__school_classes joinTable ON entity.id = joinTable.school_classes_id WHERE joinTable.school_classes_id = :id"
    )
    Flux<Subject> findBySchoolClasses(Long id);

    @Override
    <S extends Subject> Mono<S> save(S entity);

    @Override
    Flux<Subject> findAll();

    @Override
    Mono<Subject> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SubjectRepositoryInternal {
    <S extends Subject> Mono<S> save(S entity);

    Flux<Subject> findAllBy(Pageable pageable);

    Flux<Subject> findAll();

    Mono<Subject> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Subject> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Subject> findOneWithEagerRelationships(Long id);

    Flux<Subject> findAllWithEagerRelationships();

    Flux<Subject> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
