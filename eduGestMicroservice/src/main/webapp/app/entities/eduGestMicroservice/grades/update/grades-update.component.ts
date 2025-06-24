import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStudent } from 'app/entities/eduGestMicroservice/student/student.model';
import { StudentService } from 'app/entities/eduGestMicroservice/student/service/student.service';
import { ISubject } from 'app/entities/eduGestMicroservice/subject/subject.model';
import { SubjectService } from 'app/entities/eduGestMicroservice/subject/service/subject.service';
import { GradesService } from '../service/grades.service';
import { IGrades } from '../grades.model';
import { GradesFormGroup, GradesFormService } from './grades-form.service';

@Component({
  selector: 'jhi-grades-update',
  templateUrl: './grades-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class GradesUpdateComponent implements OnInit {
  isSaving = false;
  grades: IGrades | null = null;

  studentsSharedCollection: IStudent[] = [];
  subjectsSharedCollection: ISubject[] = [];

  protected gradesService = inject(GradesService);
  protected gradesFormService = inject(GradesFormService);
  protected studentService = inject(StudentService);
  protected subjectService = inject(SubjectService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: GradesFormGroup = this.gradesFormService.createGradesFormGroup();

  compareStudent = (o1: IStudent | null, o2: IStudent | null): boolean => this.studentService.compareStudent(o1, o2);

  compareSubject = (o1: ISubject | null, o2: ISubject | null): boolean => this.subjectService.compareSubject(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ grades }) => {
      this.grades = grades;
      if (grades) {
        this.updateForm(grades);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const grades = this.gradesFormService.getGrades(this.editForm);
    if (grades.id !== null) {
      this.subscribeToSaveResponse(this.gradesService.update(grades));
    } else {
      this.subscribeToSaveResponse(this.gradesService.create(grades));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGrades>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(grades: IGrades): void {
    this.grades = grades;
    this.gradesFormService.resetForm(this.editForm, grades);

    this.studentsSharedCollection = this.studentService.addStudentToCollectionIfMissing<IStudent>(
      this.studentsSharedCollection,
      grades.student,
    );
    this.subjectsSharedCollection = this.subjectService.addSubjectToCollectionIfMissing<ISubject>(
      this.subjectsSharedCollection,
      grades.subject,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.studentService
      .query()
      .pipe(map((res: HttpResponse<IStudent[]>) => res.body ?? []))
      .pipe(map((students: IStudent[]) => this.studentService.addStudentToCollectionIfMissing<IStudent>(students, this.grades?.student)))
      .subscribe((students: IStudent[]) => (this.studentsSharedCollection = students));

    this.subjectService
      .query()
      .pipe(map((res: HttpResponse<ISubject[]>) => res.body ?? []))
      .pipe(map((subjects: ISubject[]) => this.subjectService.addSubjectToCollectionIfMissing<ISubject>(subjects, this.grades?.subject)))
      .subscribe((subjects: ISubject[]) => (this.subjectsSharedCollection = subjects));
  }
}
