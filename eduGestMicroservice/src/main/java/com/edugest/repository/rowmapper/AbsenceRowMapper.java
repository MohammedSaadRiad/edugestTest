package com.edugest.repository.rowmapper;

import com.edugest.domain.Absence;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Absence}, with proper type conversions.
 */
@Service
public class AbsenceRowMapper implements BiFunction<Row, String, Absence> {

    private final ColumnConverter converter;

    public AbsenceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Absence} stored in the database.
     */
    @Override
    public Absence apply(Row row, String prefix) {
        Absence entity = new Absence();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", LocalDate.class));
        entity.setJustification(converter.fromRow(row, prefix + "_justification", String.class));
        entity.setNote(converter.fromRow(row, prefix + "_note", String.class));
        entity.setStudentId(converter.fromRow(row, prefix + "_student_id", Long.class));
        entity.setSessionId(converter.fromRow(row, prefix + "_session_id", Long.class));
        return entity;
    }
}
