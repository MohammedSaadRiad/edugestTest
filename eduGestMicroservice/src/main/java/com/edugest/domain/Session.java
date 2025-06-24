package com.edugest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Session.
 */
@Table("session")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("day")
    private String day;

    @Column("start_time")
    private String startTime;

    @Column("end_time")
    private String endTime;

    @Column("semester")
    private Integer semester;

    @org.springframework.data.annotation.Transient
    private Room room;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "schoolClasses", "teachers" }, allowSetters = true)
    private Subject subject;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "subjects", "schoolClasses" }, allowSetters = true)
    private Teacher teacher;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "timetable", "teachers", "subjects" }, allowSetters = true)
    private SchoolClass schoolClass;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "sessions", "schoolClass" }, allowSetters = true)
    private Timetable timetable;

    @Column("room_id")
    private Long roomId;

    @Column("subject_id")
    private Long subjectId;

    @Column("teacher_id")
    private Long teacherId;

    @Column("school_class_id")
    private Long schoolClassId;

    @Column("timetable_id")
    private Long timetableId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Session id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDay() {
        return this.day;
    }

    public Session day(String day) {
        this.setDay(day);
        return this;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public Session startTime(String startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public Session endTime(String endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getSemester() {
        return this.semester;
    }

    public Session semester(Integer semester) {
        this.setSemester(semester);
        return this;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
        this.roomId = room != null ? room.getId() : null;
    }

    public Session room(Room room) {
        this.setRoom(room);
        return this;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        this.subjectId = subject != null ? subject.getId() : null;
    }

    public Session subject(Subject subject) {
        this.setSubject(subject);
        return this;
    }

    public Teacher getTeacher() {
        return this.teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        this.teacherId = teacher != null ? teacher.getId() : null;
    }

    public Session teacher(Teacher teacher) {
        this.setTeacher(teacher);
        return this;
    }

    public SchoolClass getSchoolClass() {
        return this.schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
        this.schoolClassId = schoolClass != null ? schoolClass.getId() : null;
    }

    public Session schoolClass(SchoolClass schoolClass) {
        this.setSchoolClass(schoolClass);
        return this;
    }

    public Timetable getTimetable() {
        return this.timetable;
    }

    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
        this.timetableId = timetable != null ? timetable.getId() : null;
    }

    public Session timetable(Timetable timetable) {
        this.setTimetable(timetable);
        return this;
    }

    public Long getRoomId() {
        return this.roomId;
    }

    public void setRoomId(Long room) {
        this.roomId = room;
    }

    public Long getSubjectId() {
        return this.subjectId;
    }

    public void setSubjectId(Long subject) {
        this.subjectId = subject;
    }

    public Long getTeacherId() {
        return this.teacherId;
    }

    public void setTeacherId(Long teacher) {
        this.teacherId = teacher;
    }

    public Long getSchoolClassId() {
        return this.schoolClassId;
    }

    public void setSchoolClassId(Long schoolClass) {
        this.schoolClassId = schoolClass;
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
        if (!(o instanceof Session)) {
            return false;
        }
        return getId() != null && getId().equals(((Session) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Session{" +
            "id=" + getId() +
            ", day='" + getDay() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", semester=" + getSemester() +
            "}";
    }
}
