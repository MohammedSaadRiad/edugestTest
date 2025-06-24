package com.edugest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Absence.
 */
@Table("absence")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Absence implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("date")
    private LocalDate date;

    @Column("justification")
    private String justification;

    @Column("note")
    private String note;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "schoolClass", "parents" }, allowSetters = true)
    private Student student;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "room", "subject", "teacher", "schoolClass", "timetable" }, allowSetters = true)
    private Session session;

    @Column("student_id")
    private Long studentId;

    @Column("session_id")
    private Long sessionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Absence id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Absence date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getJustification() {
        return this.justification;
    }

    public Absence justification(String justification) {
        this.setJustification(justification);
        return this;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getNote() {
        return this.note;
    }

    public Absence note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
        this.studentId = student != null ? student.getId() : null;
    }

    public Absence student(Student student) {
        this.setStudent(student);
        return this;
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        this.session = session;
        this.sessionId = session != null ? session.getId() : null;
    }

    public Absence session(Session session) {
        this.setSession(session);
        return this;
    }

    public Long getStudentId() {
        return this.studentId;
    }

    public void setStudentId(Long student) {
        this.studentId = student;
    }

    public Long getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(Long session) {
        this.sessionId = session;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Absence)) {
            return false;
        }
        return getId() != null && getId().equals(((Absence) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Absence{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", justification='" + getJustification() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
