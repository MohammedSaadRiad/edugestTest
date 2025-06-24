package com.edugest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Grades.
 */
@Table("grades")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Grades implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("grade")
    private Double grade;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "schoolClass", "parents" }, allowSetters = true)
    private Student student;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "schoolClasses", "teachers" }, allowSetters = true)
    private Subject subject;

    @Column("student_id")
    private Long studentId;

    @Column("subject_id")
    private Long subjectId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Grades id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getGrade() {
        return this.grade;
    }

    public Grades grade(Double grade) {
        this.setGrade(grade);
        return this;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
        this.studentId = student != null ? student.getId() : null;
    }

    public Grades student(Student student) {
        this.setStudent(student);
        return this;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        this.subjectId = subject != null ? subject.getId() : null;
    }

    public Grades subject(Subject subject) {
        this.setSubject(subject);
        return this;
    }

    public Long getStudentId() {
        return this.studentId;
    }

    public void setStudentId(Long student) {
        this.studentId = student;
    }

    public Long getSubjectId() {
        return this.subjectId;
    }

    public void setSubjectId(Long subject) {
        this.subjectId = subject;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Grades)) {
            return false;
        }
        return getId() != null && getId().equals(((Grades) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Grades{" +
            "id=" + getId() +
            ", grade=" + getGrade() +
            "}";
    }
}
