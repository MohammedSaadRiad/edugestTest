import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISchoolClass } from 'app/entities/school-class/school-class.model';
import { SchoolClassService } from 'app/entities/school-class/service/school-class.service';
import { IParent } from 'app/entities/parent/parent.model';
import { ParentService } from 'app/entities/parent/service/parent.service';
import { Genders } from 'app/entities/enumerations/genders.model';
import { StudentService } from '../service/student.service';
import { IStudent } from '../student.model';
import { StudentFormGroup, StudentFormService } from './student-form.service';

@Component({
  selector: 'jhi-student-update',
  templateUrl: './student-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class StudentUpdateComponent implements OnInit {
  isSaving = false;
  student: IStudent | null = null;
  gendersValues = Object.keys(Genders);

  schoolClassesSharedCollection: ISchoolClass[] = [];
  parentsSharedCollection: IParent[] = [];

  protected studentService = inject(StudentService);
  protected studentFormService = inject(StudentFormService);
  protected schoolClassService = inject(SchoolClassService);
  protected parentService = inject(ParentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StudentFormGroup = this.studentFormService.createStudentFormGroup();

  compareSchoolClass = (o1: ISchoolClass | null, o2: ISchoolClass | null): boolean => this.schoolClassService.compareSchoolClass(o1, o2);

  compareParent = (o1: IParent | null, o2: IParent | null): boolean => this.parentService.compareParent(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ student }) => {
      this.student = student;
      if (student) {
        this.updateForm(student);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const student = this.studentFormService.getStudent(this.editForm);
    if (student.id !== null) {
      this.subscribeToSaveResponse(this.studentService.update(student));
    } else {
      this.subscribeToSaveResponse(this.studentService.create(student));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStudent>>): void {
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

  protected updateForm(student: IStudent): void {
    this.student = student;
    this.studentFormService.resetForm(this.editForm, student);

    this.schoolClassesSharedCollection = this.schoolClassService.addSchoolClassToCollectionIfMissing<ISchoolClass>(
      this.schoolClassesSharedCollection,
      student.schoolClass,
    );
    this.parentsSharedCollection = this.parentService.addParentToCollectionIfMissing<IParent>(
      this.parentsSharedCollection,
      ...(student.parents ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.schoolClassService
      .query()
      .pipe(map((res: HttpResponse<ISchoolClass[]>) => res.body ?? []))
      .pipe(
        map((schoolClasses: ISchoolClass[]) =>
          this.schoolClassService.addSchoolClassToCollectionIfMissing<ISchoolClass>(schoolClasses, this.student?.schoolClass),
        ),
      )
      .subscribe((schoolClasses: ISchoolClass[]) => (this.schoolClassesSharedCollection = schoolClasses));

    this.parentService
      .query()
      .pipe(map((res: HttpResponse<IParent[]>) => res.body ?? []))
      .pipe(
        map((parents: IParent[]) => this.parentService.addParentToCollectionIfMissing<IParent>(parents, ...(this.student?.parents ?? []))),
      )
      .subscribe((parents: IParent[]) => (this.parentsSharedCollection = parents));
  }
}
