import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStudent } from 'app/entities/eduGestMicroservice/student/student.model';
import { StudentService } from 'app/entities/eduGestMicroservice/student/service/student.service';
import { Genders } from 'app/entities/enumerations/genders.model';
import { ParentService } from '../service/parent.service';
import { IParent } from '../parent.model';
import { ParentFormGroup, ParentFormService } from './parent-form.service';

@Component({
  selector: 'jhi-parent-update',
  templateUrl: './parent-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ParentUpdateComponent implements OnInit {
  isSaving = false;
  parent: IParent | null = null;
  gendersValues = Object.keys(Genders);

  studentsSharedCollection: IStudent[] = [];

  protected parentService = inject(ParentService);
  protected parentFormService = inject(ParentFormService);
  protected studentService = inject(StudentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ParentFormGroup = this.parentFormService.createParentFormGroup();

  compareStudent = (o1: IStudent | null, o2: IStudent | null): boolean => this.studentService.compareStudent(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ parent }) => {
      this.parent = parent;
      if (parent) {
        this.updateForm(parent);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const parent = this.parentFormService.getParent(this.editForm);
    if (parent.id !== null) {
      this.subscribeToSaveResponse(this.parentService.update(parent));
    } else {
      this.subscribeToSaveResponse(this.parentService.create(parent));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IParent>>): void {
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

  protected updateForm(parent: IParent): void {
    this.parent = parent;
    this.parentFormService.resetForm(this.editForm, parent);

    this.studentsSharedCollection = this.studentService.addStudentToCollectionIfMissing<IStudent>(
      this.studentsSharedCollection,
      ...(parent.students ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.studentService
      .query()
      .pipe(map((res: HttpResponse<IStudent[]>) => res.body ?? []))
      .pipe(
        map((students: IStudent[]) =>
          this.studentService.addStudentToCollectionIfMissing<IStudent>(students, ...(this.parent?.students ?? [])),
        ),
      )
      .subscribe((students: IStudent[]) => (this.studentsSharedCollection = students));
  }
}
