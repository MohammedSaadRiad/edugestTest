package com.edugest.repository;

import com.edugest.domain.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Room entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoomRepository extends ReactiveCrudRepository<Room, Long>, RoomRepositoryInternal {
    @Override
    <S extends Room> Mono<S> save(S entity);

    @Override
    Flux<Room> findAll();

    @Override
    Mono<Room> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RoomRepositoryInternal {
    <S extends Room> Mono<S> save(S entity);

    Flux<Room> findAllBy(Pageable pageable);

    Flux<Room> findAll();

    Mono<Room> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Room> findAllBy(Pageable pageable, Criteria criteria);
}
