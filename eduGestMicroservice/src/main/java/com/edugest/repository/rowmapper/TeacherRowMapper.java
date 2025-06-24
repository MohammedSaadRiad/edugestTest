package com.edugest.repository.rowmapper;

import com.edugest.domain.Teacher;
import com.edugest.domain.enumeration.Genders;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Teacher}, with proper type conversions.
 */
@Service
public class TeacherRowMapper implements BiFunction<Row, String, Teacher> {

    private final ColumnConverter converter;

    public TeacherRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Teacher} stored in the database.
     */
    @Override
    public Teacher apply(Row row, String prefix) {
        Teacher entity = new Teacher();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdentifier(converter.fromRow(row, prefix + "_identifier", String.class));
        entity.setBirthDate(converter.fromRow(row, prefix + "_birth_date", LocalDate.class));
        entity.setQualification(converter.fromRow(row, prefix + "_qualification", String.class));
        entity.setGender(converter.fromRow(row, prefix + "_gender", Genders.class));
        entity.setExperience(converter.fromRow(row, prefix + "_experience", Integer.class));
        entity.setPhoneNumber(converter.fromRow(row, prefix + "_phone_number", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setType(converter.fromRow(row, prefix + "_type", String.class));
        entity.setNote(converter.fromRow(row, prefix + "_note", String.class));
        return entity;
    }
}
