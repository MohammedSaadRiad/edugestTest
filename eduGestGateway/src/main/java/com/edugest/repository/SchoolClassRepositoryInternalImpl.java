package com.edugest.repository;

import com.edugest.domain.SchoolClass;
import com.edugest.domain.Teacher;
import com.edugest.repository.rowmapper.SchoolClassRowMapper;
import com.edugest.repository.rowmapper.TimetableRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the SchoolClass entity.
 */
@SuppressWarnings("unused")
class SchoolClassRepositoryInternalImpl extends SimpleR2dbcRepository<SchoolClass, Long> implements SchoolClassRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TimetableRowMapper timetableMapper;
    private final SchoolClassRowMapper schoolclassMapper;

    private static final Table entityTable = Table.aliased("school_class", EntityManager.ENTITY_ALIAS);
    private static final Table timetableTable = Table.aliased("timetable", "timetable");

    private static final EntityManager.LinkTable teachersLink = new EntityManager.LinkTable(
        "rel_school_class__teachers",
        "school_class_id",
        "teachers_id"
    );

    public SchoolClassRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TimetableRowMapper timetableMapper,
        SchoolClassRowMapper schoolclassMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(SchoolClass.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.timetableMapper = timetableMapper;
        this.schoolclassMapper = schoolclassMapper;
    }

    @Override
    public Flux<SchoolClass> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<SchoolClass> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = SchoolClassSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TimetableSqlHelper.getColumns(timetableTable, "timetable"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(timetableTable)
            .on(Column.create("timetable_id", entityTable))
            .equals(Column.create("id", timetableTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, SchoolClass.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<SchoolClass> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<SchoolClass> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<SchoolClass> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<SchoolClass> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<SchoolClass> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private SchoolClass process(Row row, RowMetadata metadata) {
        SchoolClass entity = schoolclassMapper.apply(row, "e");
        entity.setTimetable(timetableMapper.apply(row, "timetable"));
        return entity;
    }

    @Override
    public <S extends SchoolClass> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends SchoolClass> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(teachersLink, entity.getId(), entity.getTeachers().stream().map(Teacher::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(teachersLink, entityId);
    }
}
