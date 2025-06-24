package com.edugest.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class GradesSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("grade", table, columnPrefix + "_grade"));

        columns.add(Column.aliased("student_id", table, columnPrefix + "_student_id"));
        columns.add(Column.aliased("subject_id", table, columnPrefix + "_subject_id"));
        return columns;
    }
}
