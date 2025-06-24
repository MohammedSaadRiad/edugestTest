package com.edugest.repository;

import com.edugest.domain.Absence;
import com.edugest.repository.rowmapper.AbsenceRowMapper;
import com.edugest.repository.rowmapper.SessionRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Absence entity.
 */
@SuppressWarnings("unused")
class AbsenceRepositoryInternalImpl extends SimpleR2dbcRepository<Absence, Long> implements AbsenceRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final StudentRowMapper studentMapper;
    private final SessionRowMapper sessionMapper;
    private final AbsenceRowMapper absenceMapper;

    private static final Table entityTable = Table.aliased("absence", EntityManager.ENTITY_ALIAS);
    private static final Table studentTable = Table.aliased("student", "student");
    private static final Table sessionTable = Table.aliased("session", "e_session");

    public AbsenceRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        StudentRowMapper studentMapper,
        SessionRowMapper sessionMapper,
        AbsenceRowMapper absenceMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Absence.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.studentMapper = studentMapper;
        this.sessionMapper = sessionMapper;
        this.absenceMapper = absenceMapper;
    }

    @Override
    public Flux<Absence> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Absence> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AbsenceSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(StudentSqlHelper.getColumns(studentTable, "student"));
        columns.addAll(SessionSqlHelper.getColumns(sessionTable, "session"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(studentTable)
            .on(Column.create("student_id", entityTable))
            .equals(Column.create("id", studentTable))
            .leftOuterJoin(sessionTable)
            .on(Column.create("session_id", entityTable))
            .equals(Column.create("id", sessionTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Absence.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Absence> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Absence> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Absence process(Row row, RowMetadata metadata) {
        Absence entity = absenceMapper.apply(row, "e");
        entity.setStudent(studentMapper.apply(row, "student"));
        entity.setSession(sessionMapper.apply(row, "session"));
        return entity;
    }

    @Override
    public <S extends Absence> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
