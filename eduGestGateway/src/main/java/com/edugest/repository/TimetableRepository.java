package com.edugest.repository;

import com.edugest.domain.Timetable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Timetable entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimetableRepository extends ReactiveCrudRepository<Timetable, Long>, TimetableRepositoryInternal {
    @Query("SELECT * FROM timetable entity WHERE entity.id not in (select school_class_id from school_class)")
    Flux<Timetable> findAllWhereSchoolClassIsNull();

    @Override
    <S extends Timetable> Mono<S> save(S entity);

    @Override
    Flux<Timetable> findAll();

    @Override
    Mono<Timetable> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TimetableRepositoryInternal {
    <S extends Timetable> Mono<S> save(S entity);

    Flux<Timetable> findAllBy(Pageable pageable);

    Flux<Timetable> findAll();

    Mono<Timetable> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Timetable> findAllBy(Pageable pageable, Criteria criteria);
}
