package com.edugest.repository.rowmapper;

import com.edugest.domain.Timetable;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Timetable}, with proper type conversions.
 */
@Service
public class TimetableRowMapper implements BiFunction<Row, String, Timetable> {

    private final ColumnConverter converter;

    public TimetableRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Timetable} stored in the database.
     */
    @Override
    public Timetable apply(Row row, String prefix) {
        Timetable entity = new Timetable();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setSemestre(converter.fromRow(row, prefix + "_semestre", Integer.class));
        return entity;
    }
}
