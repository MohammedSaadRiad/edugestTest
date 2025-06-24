import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStudent } from 'app/entities/eduGestMicroservice/student/student.model';
import { StudentService } from 'app/entities/eduGestMicroservice/student/service/student.service';
import { ISession } from 'app/entities/eduGestMicroservice/session/session.model';
import { SessionService } from 'app/entities/eduGestMicroservice/session/service/session.service';
import { AbsenceService } from '../service/absence.service';
import { IAbsence } from '../absence.model';
import { AbsenceFormGroup, AbsenceFormService } from './absence-form.service';

@Component({
  selector: 'jhi-absence-update',
  templateUrl: './absence-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AbsenceUpdateComponent implements OnInit {
  isSaving = false;
  absence: IAbsence | null = null;

  studentsSharedCollection: IStudent[] = [];
  sessionsSharedCollection: ISession[] = [];

  protected absenceService = inject(AbsenceService);
  protected absenceFormService = inject(AbsenceFormService);
  protected studentService = inject(StudentService);
  protected sessionService = inject(SessionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AbsenceFormGroup = this.absenceFormService.createAbsenceFormGroup();

  compareStudent = (o1: IStudent | null, o2: IStudent | null): boolean => this.studentService.compareStudent(o1, o2);

  compareSession = (o1: ISession | null, o2: ISession | null): boolean => this.sessionService.compareSession(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ absence }) => {
      this.absence = absence;
      if (absence) {
        this.updateForm(absence);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const absence = this.absenceFormService.getAbsence(this.editForm);
    if (absence.id !== null) {
      this.subscribeToSaveResponse(this.absenceService.update(absence));
    } else {
      this.subscribeToSaveResponse(this.absenceService.create(absence));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAbsence>>): void {
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

  protected updateForm(absence: IAbsence): void {
    this.absence = absence;
    this.absenceFormService.resetForm(this.editForm, absence);

    this.studentsSharedCollection = this.studentService.addStudentToCollectionIfMissing<IStudent>(
      this.studentsSharedCollection,
      absence.student,
    );
    this.sessionsSharedCollection = this.sessionService.addSessionToCollectionIfMissing<ISession>(
      this.sessionsSharedCollection,
      absence.session,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.studentService
      .query()
      .pipe(map((res: HttpResponse<IStudent[]>) => res.body ?? []))
      .pipe(map((students: IStudent[]) => this.studentService.addStudentToCollectionIfMissing<IStudent>(students, this.absence?.student)))
      .subscribe((students: IStudent[]) => (this.studentsSharedCollection = students));

    this.sessionService
      .query()
      .pipe(map((res: HttpResponse<ISession[]>) => res.body ?? []))
      .pipe(map((sessions: ISession[]) => this.sessionService.addSessionToCollectionIfMissing<ISession>(sessions, this.absence?.session)))
      .subscribe((sessions: ISession[]) => (this.sessionsSharedCollection = sessions));
  }
}
