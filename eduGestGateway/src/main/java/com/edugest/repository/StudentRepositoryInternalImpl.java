package com.edugest.repository;

import com.edugest.domain.Parent;
import com.edugest.domain.Student;
import com.edugest.repository.rowmapper.SchoolClassRowMapper;
import com.edugest.repository.rowmapper.StudentRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Student entity.
 */
@SuppressWarnings("unused")
class StudentRepositoryInternalImpl extends SimpleR2dbcRepository<Student, Long> implements StudentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final SchoolClassRowMapper schoolclassMapper;
    private final StudentRowMapper studentMapper;

    private static final Table entityTable = Table.aliased("student", EntityManager.ENTITY_ALIAS);
    private static final Table schoolClassTable = Table.aliased("school_class", "schoolClass");

    private static final EntityManager.LinkTable parentsLink = new EntityManager.LinkTable(
        "rel_student__parents",
        "student_id",
        "parents_id"
    );

    public StudentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        SchoolClassRowMapper schoolclassMapper,
        StudentRowMapper studentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Student.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.schoolclassMapper = schoolclassMapper;
        this.studentMapper = studentMapper;
    }

    @Override
    public Flux<Student> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Student> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = StudentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(SchoolClassSqlHelper.getColumns(schoolClassTable, "schoolClass"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(schoolClassTable)
            .on(Column.create("school_class_id", entityTable))
            .equals(Column.create("id", schoolClassTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Student.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Student> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Student> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Student> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Student> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Student> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Student process(Row row, RowMetadata metadata) {
        Student entity = studentMapper.apply(row, "e");
        entity.setSchoolClass(schoolclassMapper.apply(row, "schoolClass"));
        return entity;
    }

    @Override
    public <S extends Student> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Student> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(parentsLink, entity.getId(), entity.getParents().stream().map(Parent::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(parentsLink, entityId);
    }
}
