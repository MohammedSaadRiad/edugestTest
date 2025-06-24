import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISubject } from 'app/entities/subject/subject.model';
import { SubjectService } from 'app/entities/subject/service/subject.service';
import { ISchoolClass } from 'app/entities/school-class/school-class.model';
import { SchoolClassService } from 'app/entities/school-class/service/school-class.service';
import { Genders } from 'app/entities/enumerations/genders.model';
import { TeacherService } from '../service/teacher.service';
import { ITeacher } from '../teacher.model';
import { TeacherFormGroup, TeacherFormService } from './teacher-form.service';

@Component({
  selector: 'jhi-teacher-update',
  templateUrl: './teacher-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TeacherUpdateComponent implements OnInit {
  isSaving = false;
  teacher: ITeacher | null = null;
  gendersValues = Object.keys(Genders);

  subjectsSharedCollection: ISubject[] = [];
  schoolClassesSharedCollection: ISchoolClass[] = [];

  protected teacherService = inject(TeacherService);
  protected teacherFormService = inject(TeacherFormService);
  protected subjectService = inject(SubjectService);
  protected schoolClassService = inject(SchoolClassService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TeacherFormGroup = this.teacherFormService.createTeacherFormGroup();

  compareSubject = (o1: ISubject | null, o2: ISubject | null): boolean => this.subjectService.compareSubject(o1, o2);

  compareSchoolClass = (o1: ISchoolClass | null, o2: ISchoolClass | null): boolean => this.schoolClassService.compareSchoolClass(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teacher }) => {
      this.teacher = teacher;
      if (teacher) {
        this.updateForm(teacher);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teacher = this.teacherFormService.getTeacher(this.editForm);
    if (teacher.id !== null) {
      this.subscribeToSaveResponse(this.teacherService.update(teacher));
    } else {
      this.subscribeToSaveResponse(this.teacherService.create(teacher));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeacher>>): void {
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

  protected updateForm(teacher: ITeacher): void {
    this.teacher = teacher;
    this.teacherFormService.resetForm(this.editForm, teacher);

    this.subjectsSharedCollection = this.subjectService.addSubjectToCollectionIfMissing<ISubject>(
      this.subjectsSharedCollection,
      ...(teacher.subjects ?? []),
    );
    this.schoolClassesSharedCollection = this.schoolClassService.addSchoolClassToCollectionIfMissing<ISchoolClass>(
      this.schoolClassesSharedCollection,
      ...(teacher.schoolClasses ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.subjectService
      .query()
      .pipe(map((res: HttpResponse<ISubject[]>) => res.body ?? []))
      .pipe(
        map((subjects: ISubject[]) =>
          this.subjectService.addSubjectToCollectionIfMissing<ISubject>(subjects, ...(this.teacher?.subjects ?? [])),
        ),
      )
      .subscribe((subjects: ISubject[]) => (this.subjectsSharedCollection = subjects));

    this.schoolClassService
      .query()
      .pipe(map((res: HttpResponse<ISchoolClass[]>) => res.body ?? []))
      .pipe(
        map((schoolClasses: ISchoolClass[]) =>
          this.schoolClassService.addSchoolClassToCollectionIfMissing<ISchoolClass>(schoolClasses, ...(this.teacher?.schoolClasses ?? [])),
        ),
      )
      .subscribe((schoolClasses: ISchoolClass[]) => (this.schoolClassesSharedCollection = schoolClasses));
  }
}
