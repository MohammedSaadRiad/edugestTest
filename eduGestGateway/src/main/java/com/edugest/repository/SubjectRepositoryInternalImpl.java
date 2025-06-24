package com.edugest.repository;

import com.edugest.domain.SchoolClass;
import com.edugest.domain.Subject;
import com.edugest.repository.rowmapper.SubjectRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Subject entity.
 */
@SuppressWarnings("unused")
class SubjectRepositoryInternalImpl extends SimpleR2dbcRepository<Subject, Long> implements SubjectRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final SubjectRowMapper subjectMapper;

    private static final Table entityTable = Table.aliased("subject", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable schoolClassesLink = new EntityManager.LinkTable(
        "rel_subject__school_classes",
        "subject_id",
        "school_classes_id"
    );

    public SubjectRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        SubjectRowMapper subjectMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Subject.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.subjectMapper = subjectMapper;
    }

    @Override
    public Flux<Subject> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Subject> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = SubjectSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Subject.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Subject> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Subject> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Subject> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Subject> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Subject> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Subject process(Row row, RowMetadata metadata) {
        Subject entity = subjectMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Subject> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Subject> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(schoolClassesLink, entity.getId(), entity.getSchoolClasses().stream().map(SchoolClass::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(schoolClassesLink, entityId);
    }
}
