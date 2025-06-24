package com.edugest.repository.rowmapper;

import com.edugest.domain.Room;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Room}, with proper type conversions.
 */
@Service
public class RoomRowMapper implements BiFunction<Row, String, Room> {

    private final ColumnConverter converter;

    public RoomRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Room} stored in the database.
     */
    @Override
    public Room apply(Row row, String prefix) {
        Room entity = new Room();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCapacity(converter.fromRow(row, prefix + "_capacity", Integer.class));
        return entity;
    }
}
