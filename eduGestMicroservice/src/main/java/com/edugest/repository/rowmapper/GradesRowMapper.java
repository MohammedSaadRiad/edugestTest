package com.edugest.repository.rowmapper;

import com.edugest.domain.Grades;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Grades}, with proper type conversions.
 */
@Service
public class GradesRowMapper implements BiFunction<Row, String, Grades> {

    private final ColumnConverter converter;

    public GradesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Grades} stored in the database.
     */
    @Override
    public Grades apply(Row row, String prefix) {
        Grades entity = new Grades();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setGrade(converter.fromRow(row, prefix + "_grade", Double.class));
        entity.setStudentId(converter.fromRow(row, prefix + "_student_id", Long.class));
        entity.setSubjectId(converter.fromRow(row, prefix + "_subject_id", Long.class));
        return entity;
    }
}
