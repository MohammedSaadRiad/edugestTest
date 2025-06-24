import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITimetable } from 'app/entities/timetable/timetable.model';
import { TimetableService } from 'app/entities/timetable/service/timetable.service';
import { ITeacher } from 'app/entities/teacher/teacher.model';
import { TeacherService } from 'app/entities/teacher/service/teacher.service';
import { ISubject } from 'app/entities/subject/subject.model';
import { SubjectService } from 'app/entities/subject/service/subject.service';
import { SchoolClassService } from '../service/school-class.service';
import { ISchoolClass } from '../school-class.model';
import { SchoolClassFormGroup, SchoolClassFormService } from './school-class-form.service';

@Component({
  selector: 'jhi-school-class-update',
  templateUrl: './school-class-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SchoolClassUpdateComponent implements OnInit {
  isSaving = false;
  schoolClass: ISchoolClass | null = null;

  timetablesCollection: ITimetable[] = [];
  teachersSharedCollection: ITeacher[] = [];
  subjectsSharedCollection: ISubject[] = [];

  protected schoolClassService = inject(SchoolClassService);
  protected schoolClassFormService = inject(SchoolClassFormService);
  protected timetableService = inject(TimetableService);
  protected teacherService = inject(TeacherService);
  protected subjectService = inject(SubjectService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SchoolClassFormGroup = this.schoolClassFormService.createSchoolClassFormGroup();

  compareTimetable = (o1: ITimetable | null, o2: ITimetable | null): boolean => this.timetableService.compareTimetable(o1, o2);

  compareTeacher = (o1: ITeacher | null, o2: ITeacher | null): boolean => this.teacherService.compareTeacher(o1, o2);

  compareSubject = (o1: ISubject | null, o2: ISubject | null): boolean => this.subjectService.compareSubject(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ schoolClass }) => {
      this.schoolClass = schoolClass;
      if (schoolClass) {
        this.updateForm(schoolClass);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const schoolClass = this.schoolClassFormService.getSchoolClass(this.editForm);
    if (schoolClass.id !== null) {
      this.subscribeToSaveResponse(this.schoolClassService.update(schoolClass));
    } else {
      this.subscribeToSaveResponse(this.schoolClassService.create(schoolClass));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISchoolClass>>): void {
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

  protected updateForm(schoolClass: ISchoolClass): void {
    this.schoolClass = schoolClass;
    this.schoolClassFormService.resetForm(this.editForm, schoolClass);

    this.timetablesCollection = this.timetableService.addTimetableToCollectionIfMissing<ITimetable>(
      this.timetablesCollection,
      schoolClass.timetable,
    );
    this.teachersSharedCollection = this.teacherService.addTeacherToCollectionIfMissing<ITeacher>(
      this.teachersSharedCollection,
      ...(schoolClass.teachers ?? []),
    );
    this.subjectsSharedCollection = this.subjectService.addSubjectToCollectionIfMissing<ISubject>(
      this.subjectsSharedCollection,
      ...(schoolClass.subjects ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.timetableService
      .query({ filter: 'schoolclass-is-null' })
      .pipe(map((res: HttpResponse<ITimetable[]>) => res.body ?? []))
      .pipe(
        map((timetables: ITimetable[]) =>
          this.timetableService.addTimetableToCollectionIfMissing<ITimetable>(timetables, this.schoolClass?.timetable),
        ),
      )
      .subscribe((timetables: ITimetable[]) => (this.timetablesCollection = timetables));

    this.teacherService
      .query()
      .pipe(map((res: HttpResponse<ITeacher[]>) => res.body ?? []))
      .pipe(
        map((teachers: ITeacher[]) =>
          this.teacherService.addTeacherToCollectionIfMissing<ITeacher>(teachers, ...(this.schoolClass?.teachers ?? [])),
        ),
      )
      .subscribe((teachers: ITeacher[]) => (this.teachersSharedCollection = teachers));

    this.subjectService
      .query()
      .pipe(map((res: HttpResponse<ISubject[]>) => res.body ?? []))
      .pipe(
        map((subjects: ISubject[]) =>
          this.subjectService.addSubjectToCollectionIfMissing<ISubject>(subjects, ...(this.schoolClass?.subjects ?? [])),
        ),
      )
      .subscribe((subjects: ISubject[]) => (this.subjectsSharedCollection = subjects));
  }
}
