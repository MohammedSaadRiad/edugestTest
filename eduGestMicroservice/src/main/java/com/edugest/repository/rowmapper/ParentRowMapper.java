package com.edugest.repository.rowmapper;

import com.edugest.domain.Parent;
import com.edugest.domain.enumeration.Genders;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Parent}, with proper type conversions.
 */
@Service
public class ParentRowMapper implements BiFunction<Row, String, Parent> {

    private final ColumnConverter converter;

    public ParentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Parent} stored in the database.
     */
    @Override
    public Parent apply(Row row, String prefix) {
        Parent entity = new Parent();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdentifier(converter.fromRow(row, prefix + "_identifier", String.class));
        entity.setBirthDate(converter.fromRow(row, prefix + "_birth_date", LocalDate.class));
        entity.setGender(converter.fromRow(row, prefix + "_gender", Genders.class));
        entity.setPhoneNumber(converter.fromRow(row, prefix + "_phone_number", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setNote(converter.fromRow(row, prefix + "_note", String.class));
        return entity;
    }
}
