package com.edugest.repository;

import com.edugest.domain.Grades;
import com.edugest.repository.rowmapper.GradesRowMapper;
import com.edugest.repository.rowmapper.StudentRowMapper;
import com.edugest.repository.rowmapper.SubjectRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Grades entity.
 */
@SuppressWarnings("unused")
class GradesRepositoryInternalImpl extends SimpleR2dbcRepository<Grades, Long> implements GradesRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final StudentRowMapper studentMapper;
    private final SubjectRowMapper subjectMapper;
    private final GradesRowMapper gradesMapper;

    private static final Table entityTable = Table.aliased("grades", EntityManager.ENTITY_ALIAS);
    private static final Table studentTable = Table.aliased("student", "student");
    private static final Table subjectTable = Table.aliased("subject", "subject");

    public GradesRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        StudentRowMapper studentMapper,
        SubjectRowMapper subjectMapper,
        GradesRowMapper gradesMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Grades.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.studentMapper = studentMapper;
        this.subjectMapper = subjectMapper;
        this.gradesMapper = gradesMapper;
    }

    @Override
    public Flux<Grades> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Grades> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = GradesSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(StudentSqlHelper.getColumns(studentTable, "student"));
        columns.addAll(SubjectSqlHelper.getColumns(subjectTable, "subject"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(studentTable)
            .on(Column.create("student_id", entityTable))
            .equals(Column.create("id", studentTable))
            .leftOuterJoin(subjectTable)
            .on(Column.create("subject_id", entityTable))
            .equals(Column.create("id", subjectTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Grades.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Grades> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Grades> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Grades process(Row row, RowMetadata metadata) {
        Grades entity = gradesMapper.apply(row, "e");
        entity.setStudent(studentMapper.apply(row, "student"));
        entity.setSubject(subjectMapper.apply(row, "subject"));
        return entity;
    }

    @Override
    public <S extends Grades> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
