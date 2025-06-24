package com.edugest.repository;

import com.edugest.domain.Administration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Administration entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdministrationRepository extends ReactiveCrudRepository<Administration, Long>, AdministrationRepositoryInternal {
    @Override
    <S extends Administration> Mono<S> save(S entity);

    @Override
    Flux<Administration> findAll();

    @Override
    Mono<Administration> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AdministrationRepositoryInternal {
    <S extends Administration> Mono<S> save(S entity);

    Flux<Administration> findAllBy(Pageable pageable);

    Flux<Administration> findAll();

    Mono<Administration> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Administration> findAllBy(Pageable pageable, Criteria criteria);
}
