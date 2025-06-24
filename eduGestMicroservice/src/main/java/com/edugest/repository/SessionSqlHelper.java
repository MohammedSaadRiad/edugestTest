package com.edugest.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class SessionSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("day", table, columnPrefix + "_day"));
        columns.add(Column.aliased("start_time", table, columnPrefix + "_start_time"));
        columns.add(Column.aliased("end_time", table, columnPrefix + "_end_time"));
        columns.add(Column.aliased("semester", table, columnPrefix + "_semester"));

        columns.add(Column.aliased("room_id", table, columnPrefix + "_room_id"));
        columns.add(Column.aliased("subject_id", table, columnPrefix + "_subject_id"));
        columns.add(Column.aliased("teacher_id", table, columnPrefix + "_teacher_id"));
        columns.add(Column.aliased("school_class_id", table, columnPrefix + "_school_class_id"));
        columns.add(Column.aliased("timetable_id", table, columnPrefix + "_timetable_id"));
        return columns;
    }
}
