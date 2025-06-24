package com.edugest.repository;

import com.edugest.domain.Session;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Session entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SessionRepository extends ReactiveCrudRepository<Session, Long>, SessionRepositoryInternal {
    @Query("SELECT * FROM session entity WHERE entity.room_id = :id")
    Flux<Session> findByRoom(Long id);

    @Query("SELECT * FROM session entity WHERE entity.room_id IS NULL")
    Flux<Session> findAllWhereRoomIsNull();

    @Query("SELECT * FROM session entity WHERE entity.subject_id = :id")
    Flux<Session> findBySubject(Long id);

    @Query("SELECT * FROM session entity WHERE entity.subject_id IS NULL")
    Flux<Session> findAllWhereSubjectIsNull();

    @Query("SELECT * FROM session entity WHERE entity.teacher_id = :id")
    Flux<Session> findByTeacher(Long id);

    @Query("SELECT * FROM session entity WHERE entity.teacher_id IS NULL")
    Flux<Session> findAllWhereTeacherIsNull();

    @Query("SELECT * FROM session entity WHERE entity.school_class_id = :id")
    Flux<Session> findBySchoolClass(Long id);

    @Query("SELECT * FROM session entity WHERE entity.school_class_id IS NULL")
    Flux<Session> findAllWhereSchoolClassIsNull();

    @Query("SELECT * FROM session entity WHERE entity.timetable_id = :id")
    Flux<Session> findByTimetable(Long id);

    @Query("SELECT * FROM session entity WHERE entity.timetable_id IS NULL")
    Flux<Session> findAllWhereTimetableIsNull();

    @Override
    <S extends Session> Mono<S> save(S entity);

    @Override
    Flux<Session> findAll();

    @Override
    Mono<Session> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SessionRepositoryInternal {
    <S extends Session> Mono<S> save(S entity);

    Flux<Session> findAllBy(Pageable pageable);

    Flux<Session> findAll();

    Mono<Session> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Session> findAllBy(Pageable pageable, Criteria criteria);
}
