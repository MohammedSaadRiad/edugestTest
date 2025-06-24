import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISchoolClass } from 'app/entities/school-class/school-class.model';
import { SchoolClassService } from 'app/entities/school-class/service/school-class.service';
import { ITeacher } from 'app/entities/teacher/teacher.model';
import { TeacherService } from 'app/entities/teacher/service/teacher.service';
import { SubjectService } from '../service/subject.service';
import { ISubject } from '../subject.model';
import { SubjectFormGroup, SubjectFormService } from './subject-form.service';

@Component({
  selector: 'jhi-subject-update',
  templateUrl: './subject-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SubjectUpdateComponent implements OnInit {
  isSaving = false;
  subject: ISubject | null = null;

  schoolClassesSharedCollection: ISchoolClass[] = [];
  teachersSharedCollection: ITeacher[] = [];

  protected subjectService = inject(SubjectService);
  protected subjectFormService = inject(SubjectFormService);
  protected schoolClassService = inject(SchoolClassService);
  protected teacherService = inject(TeacherService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SubjectFormGroup = this.subjectFormService.createSubjectFormGroup();

  compareSchoolClass = (o1: ISchoolClass | null, o2: ISchoolClass | null): boolean => this.schoolClassService.compareSchoolClass(o1, o2);

  compareTeacher = (o1: ITeacher | null, o2: ITeacher | null): boolean => this.teacherService.compareTeacher(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ subject }) => {
      this.subject = subject;
      if (subject) {
        this.updateForm(subject);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const subject = this.subjectFormService.getSubject(this.editForm);
    if (subject.id !== null) {
      this.subscribeToSaveResponse(this.subjectService.update(subject));
    } else {
      this.subscribeToSaveResponse(this.subjectService.create(subject));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISubject>>): void {
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

  protected updateForm(subject: ISubject): void {
    this.subject = subject;
    this.subjectFormService.resetForm(this.editForm, subject);

    this.schoolClassesSharedCollection = this.schoolClassService.addSchoolClassToCollectionIfMissing<ISchoolClass>(
      this.schoolClassesSharedCollection,
      ...(subject.schoolClasses ?? []),
    );
    this.teachersSharedCollection = this.teacherService.addTeacherToCollectionIfMissing<ITeacher>(
      this.teachersSharedCollection,
      ...(subject.teachers ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.schoolClassService
      .query()
      .pipe(map((res: HttpResponse<ISchoolClass[]>) => res.body ?? []))
      .pipe(
        map((schoolClasses: ISchoolClass[]) =>
          this.schoolClassService.addSchoolClassToCollectionIfMissing<ISchoolClass>(schoolClasses, ...(this.subject?.schoolClasses ?? [])),
        ),
      )
      .subscribe((schoolClasses: ISchoolClass[]) => (this.schoolClassesSharedCollection = schoolClasses));

    this.teacherService
      .query()
      .pipe(map((res: HttpResponse<ITeacher[]>) => res.body ?? []))
      .pipe(
        map((teachers: ITeacher[]) =>
          this.teacherService.addTeacherToCollectionIfMissing<ITeacher>(teachers, ...(this.subject?.teachers ?? [])),
        ),
      )
      .subscribe((teachers: ITeacher[]) => (this.teachersSharedCollection = teachers));
  }
}
