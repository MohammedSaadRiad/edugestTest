package com.edugest.repository.rowmapper;

import com.edugest.domain.Session;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Session}, with proper type conversions.
 */
@Service
public class SessionRowMapper implements BiFunction<Row, String, Session> {

    private final ColumnConverter converter;

    public SessionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Session} stored in the database.
     */
    @Override
    public Session apply(Row row, String prefix) {
        Session entity = new Session();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDay(converter.fromRow(row, prefix + "_day", String.class));
        entity.setStartTime(converter.fromRow(row, prefix + "_start_time", String.class));
        entity.setEndTime(converter.fromRow(row, prefix + "_end_time", String.class));
        entity.setSemester(converter.fromRow(row, prefix + "_semester", Integer.class));
        entity.setRoomId(converter.fromRow(row, prefix + "_room_id", Long.class));
        entity.setSubjectId(converter.fromRow(row, prefix + "_subject_id", Long.class));
        entity.setTeacherId(converter.fromRow(row, prefix + "_teacher_id", Long.class));
        entity.setSchoolClassId(converter.fromRow(row, prefix + "_school_class_id", Long.class));
        entity.setTimetableId(converter.fromRow(row, prefix + "_timetable_id", Long.class));
        return entity;
    }
}
