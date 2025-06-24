package com.edugest.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AbsenceSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("date", table, columnPrefix + "_date"));
        columns.add(Column.aliased("justification", table, columnPrefix + "_justification"));
        columns.add(Column.aliased("note", table, columnPrefix + "_note"));

        columns.add(Column.aliased("student_id", table, columnPrefix + "_student_id"));
        columns.add(Column.aliased("session_id", table, columnPrefix + "_session_id"));
        return columns;
    }
}
