package com.edugest.repository;

import com.edugest.domain.Room;
import com.edugest.repository.rowmapper.RoomRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Room entity.
 */
@SuppressWarnings("unused")
class RoomRepositoryInternalImpl extends SimpleR2dbcRepository<Room, Long> implements RoomRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RoomRowMapper roomMapper;

    private static final Table entityTable = Table.aliased("room", EntityManager.ENTITY_ALIAS);

    public RoomRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RoomRowMapper roomMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Room.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.roomMapper = roomMapper;
    }

    @Override
    public Flux<Room> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Room> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RoomSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Room.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Room> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Room> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Room process(Row row, RowMetadata metadata) {
        Room entity = roomMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Room> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
