package com.edugest.repository;

import com.edugest.domain.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Student entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StudentRepository extends ReactiveCrudRepository<Student, Long>, StudentRepositoryInternal {
    @Override
    Mono<Student> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Student> findAllWithEagerRelationships();

    @Override
    Flux<Student> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM student entity WHERE entity.school_class_id = :id")
    Flux<Student> findBySchoolClass(Long id);

    @Query("SELECT * FROM student entity WHERE entity.school_class_id IS NULL")
    Flux<Student> findAllWhereSchoolClassIsNull();

    @Query(
        "SELECT entity.* FROM student entity JOIN rel_student__parents joinTable ON entity.id = joinTable.parents_id WHERE joinTable.parents_id = :id"
    )
    Flux<Student> findByParents(Long id);

    @Override
    <S extends Student> Mono<S> save(S entity);

    @Override
    Flux<Student> findAll();

    @Override
    Mono<Student> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface StudentRepositoryInternal {
    <S extends Student> Mono<S> save(S entity);

    Flux<Student> findAllBy(Pageable pageable);

    Flux<Student> findAll();

    Mono<Student> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Student> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Student> findOneWithEagerRelationships(Long id);

    Flux<Student> findAllWithEagerRelationships();

    Flux<Student> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
