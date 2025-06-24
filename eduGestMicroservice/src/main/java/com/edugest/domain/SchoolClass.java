package com.edugest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A SchoolClass.
 */
@Table("school_class")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SchoolClass implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Column("year")
    private Integer year;

    @org.springframework.data.annotation.Transient
    private Timetable timetable;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "subjects", "schoolClasses" }, allowSetters = true)
    private Set<Teacher> teachers = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "schoolClasses", "teachers" }, allowSetters = true)
    private Set<Subject> subjects = new HashSet<>();

    @Column("timetable_id")
    private Long timetableId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SchoolClass id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public SchoolClass name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return this.year;
    }

    public SchoolClass year(Integer year) {
        this.setYear(year);
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Timetable getTimetable() {
        return this.timetable;
    }

    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
        this.timetableId = timetable != null ? timetable.getId() : null;
    }

    public SchoolClass timetable(Timetable timetable) {
        this.setTimetable(timetable);
        return this;
    }

    public Set<Teacher> getTeachers() {
        return this.teachers;
    }

    public void setTeachers(Set<Teacher> teachers) {
        this.teachers = teachers;
    }

    public SchoolClass teachers(Set<Teacher> teachers) {
        this.setTeachers(teachers);
        return this;
    }

    public SchoolClass addTeachers(Teacher teacher) {
        this.teachers.add(teacher);
        return this;
    }

    public SchoolClass removeTeachers(Teacher teacher) {
        this.teachers.remove(teacher);
        return this;
    }

    public Set<Subject> getSubjects() {
        return this.subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        if (this.subjects != null) {
            this.subjects.forEach(i -> i.removeSchoolClasses(this));
        }
        if (subjects != null) {
            subjects.forEach(i -> i.addSchoolClasses(this));
        }
        this.subjects = subjects;
    }

    public SchoolClass subjects(Set<Subject> subjects) {
        this.setSubjects(subjects);
        return this;
    }

    public SchoolClass addSubjects(Subject subject) {
        this.subjects.add(subject);
        subject.getSchoolClasses().add(this);
        return this;
    }

    public SchoolClass removeSubjects(Subject subject) {
        this.subjects.remove(subject);
        subject.getSchoolClasses().remove(this);
        return this;
    }

    public Long getTimetableId() {
        return this.timetableId;
    }

    public void setTimetableId(Long timetable) {
        this.timetableId = timetable;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SchoolClass)) {
            return false;
        }
        return getId() != null && getId().equals(((SchoolClass) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SchoolClass{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", year=" + getYear() +
            "}";
    }
}
