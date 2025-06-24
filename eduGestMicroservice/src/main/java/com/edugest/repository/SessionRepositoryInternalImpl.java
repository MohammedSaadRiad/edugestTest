package com.edugest.repository;

import com.edugest.domain.Session;
import com.edugest.repository.rowmapper.RoomRowMapper;
import com.edugest.repository.rowmapper.SchoolClassRowMapper;
import com.edugest.repository.rowmapper.SessionRowMapper;
import com.edugest.repository.rowmapper.SubjectRowMapper;
import com.edugest.repository.rowmapper.TeacherRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Session entity.
 */
@SuppressWarnings("unused")
class SessionRepositoryInternalImpl extends SimpleR2dbcRepository<Session, Long> implements SessionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RoomRowMapper roomMapper;
    private final SubjectRowMapper subjectMapper;
    private final TeacherRowMapper teacherMapper;
    private final SchoolClassRowMapper schoolclassMapper;
    private final TimetableRowMapper timetableMapper;
    private final SessionRowMapper sessionMapper;

    private static final Table entityTable = Table.aliased("session", EntityManager.ENTITY_ALIAS);
    private static final Table roomTable = Table.aliased("room", "room");
    private static final Table subjectTable = Table.aliased("subject", "subject");
    private static final Table teacherTable = Table.aliased("teacher", "teacher");
    private static final Table schoolClassTable = Table.aliased("school_class", "schoolClass");
    private static final Table timetableTable = Table.aliased("timetable", "timetable");

    public SessionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RoomRowMapper roomMapper,
        SubjectRowMapper subjectMapper,
        TeacherRowMapper teacherMapper,
        SchoolClassRowMapper schoolclassMapper,
        TimetableRowMapper timetableMapper,
        SessionRowMapper sessionMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Session.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.roomMapper = roomMapper;
        this.subjectMapper = subjectMapper;
        this.teacherMapper = teacherMapper;
        this.schoolclassMapper = schoolclassMapper;
        this.timetableMapper = timetableMapper;
        this.sessionMapper = sessionMapper;
    }

    @Override
    public Flux<Session> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Session> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = SessionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RoomSqlHelper.getColumns(roomTable, "room"));
        columns.addAll(SubjectSqlHelper.getColumns(subjectTable, "subject"));
        columns.addAll(TeacherSqlHelper.getColumns(teacherTable, "teacher"));
        columns.addAll(SchoolClassSqlHelper.getColumns(schoolClassTable, "schoolClass"));
        columns.addAll(TimetableSqlHelper.getColumns(timetableTable, "timetable"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(roomTable)
            .on(Column.create("room_id", entityTable))
            .equals(Column.create("id", roomTable))
            .leftOuterJoin(subjectTable)
            .on(Column.create("subject_id", entityTable))
            .equals(Column.create("id", subjectTable))
            .leftOuterJoin(teacherTable)
            .on(Column.create("teacher_id", entityTable))
            .equals(Column.create("id", teacherTable))
            .leftOuterJoin(schoolClassTable)
            .on(Column.create("school_class_id", entityTable))
            .equals(Column.create("id", schoolClassTable))
            .leftOuterJoin(timetableTable)
            .on(Column.create("timetable_id", entityTable))
            .equals(Column.create("id", timetableTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Session.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Session> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Session> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Session process(Row row, RowMetadata metadata) {
        Session entity = sessionMapper.apply(row, "e");
        entity.setRoom(roomMapper.apply(row, "room"));
        entity.setSubject(subjectMapper.apply(row, "subject"));
        entity.setTeacher(teacherMapper.apply(row, "teacher"));
        entity.setSchoolClass(schoolclassMapper.apply(row, "schoolClass"));
        entity.setTimetable(timetableMapper.apply(row, "timetable"));
        return entity;
    }

    @Override
    public <S extends Session> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
