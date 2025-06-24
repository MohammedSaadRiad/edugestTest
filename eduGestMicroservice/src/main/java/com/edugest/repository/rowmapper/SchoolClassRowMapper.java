package com.edugest.repository.rowmapper;

import com.edugest.domain.SchoolClass;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link SchoolClass}, with proper type conversions.
 */
@Service
public class SchoolClassRowMapper implements BiFunction<Row, String, SchoolClass> {

    private final ColumnConverter converter;

    public SchoolClassRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link SchoolClass} stored in the database.
     */
    @Override
    public SchoolClass apply(Row row, String prefix) {
        SchoolClass entity = new SchoolClass();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setYear(converter.fromRow(row, prefix + "_year", Integer.class));
        entity.setTimetableId(converter.fromRow(row, prefix + "_timetable_id", Long.class));
        return entity;
    }
}
