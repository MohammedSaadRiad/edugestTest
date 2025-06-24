package com.edugest.repository.rowmapper;

import com.edugest.domain.Subject;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Subject}, with proper type conversions.
 */
@Service
public class SubjectRowMapper implements BiFunction<Row, String, Subject> {

    private final ColumnConverter converter;

    public SubjectRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Subject} stored in the database.
     */
    @Override
    public Subject apply(Row row, String prefix) {
        Subject entity = new Subject();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
