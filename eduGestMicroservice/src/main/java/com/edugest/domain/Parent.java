package com.edugest.domain;

import com.edugest.domain.enumeration.Genders;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Parent.
 */
@Table("parent")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Parent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("identifier")
    private String identifier;

    @Column("birth_date")
    private LocalDate birthDate;

    @Column("gender")
    private Genders gender;

    @Column("phone_number")
    private String phoneNumber;

    @Column("address")
    private String address;

    @Column("note")
    private String note;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "schoolClass", "parents" }, allowSetters = true)
    private Set<Student> students = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Parent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Parent identifier(String identifier) {
        this.setIdentifier(identifier);
        return this;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public Parent birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Genders getGender() {
        return this.gender;
    }

    public Parent gender(Genders gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Genders gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Parent phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public Parent address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return this.note;
    }

    public Parent note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<Student> getStudents() {
        return this.students;
    }

    public void setStudents(Set<Student> students) {
        if (this.students != null) {
            this.students.forEach(i -> i.removeParents(this));
        }
        if (students != null) {
            students.forEach(i -> i.addParents(this));
        }
        this.students = students;
    }

    public Parent students(Set<Student> students) {
        this.setStudents(students);
        return this;
    }

    public Parent addStudents(Student student) {
        this.students.add(student);
        student.getParents().add(this);
        return this;
    }

    public Parent removeStudents(Student student) {
        this.students.remove(student);
        student.getParents().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Parent)) {
            return false;
        }
        return getId() != null && getId().equals(((Parent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Parent{" +
            "id=" + getId() +
            ", identifier='" + getIdentifier() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", gender='" + getGender() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", address='" + getAddress() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
