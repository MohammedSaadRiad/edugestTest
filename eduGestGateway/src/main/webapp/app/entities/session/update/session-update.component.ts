import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IRoom } from 'app/entities/room/room.model';
import { RoomService } from 'app/entities/room/service/room.service';
import { ISubject } from 'app/entities/subject/subject.model';
import { SubjectService } from 'app/entities/subject/service/subject.service';
import { ITeacher } from 'app/entities/teacher/teacher.model';
import { TeacherService } from 'app/entities/teacher/service/teacher.service';
import { ISchoolClass } from 'app/entities/school-class/school-class.model';
import { SchoolClassService } from 'app/entities/school-class/service/school-class.service';
import { ITimetable } from 'app/entities/timetable/timetable.model';
import { TimetableService } from 'app/entities/timetable/service/timetable.service';
import { SessionService } from '../service/session.service';
import { ISession } from '../session.model';
import { SessionFormGroup, SessionFormService } from './session-form.service';

@Component({
  selector: 'jhi-session-update',
  templateUrl: './session-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SessionUpdateComponent implements OnInit {
  isSaving = false;
  session: ISession | null = null;

  roomsSharedCollection: IRoom[] = [];
  subjectsSharedCollection: ISubject[] = [];
  teachersSharedCollection: ITeacher[] = [];
  schoolClassesSharedCollection: ISchoolClass[] = [];
  timetablesSharedCollection: ITimetable[] = [];

  protected sessionService = inject(SessionService);
  protected sessionFormService = inject(SessionFormService);
  protected roomService = inject(RoomService);
  protected subjectService = inject(SubjectService);
  protected teacherService = inject(TeacherService);
  protected schoolClassService = inject(SchoolClassService);
  protected timetableService = inject(TimetableService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SessionFormGroup = this.sessionFormService.createSessionFormGroup();

  compareRoom = (o1: IRoom | null, o2: IRoom | null): boolean => this.roomService.compareRoom(o1, o2);

  compareSubject = (o1: ISubject | null, o2: ISubject | null): boolean => this.subjectService.compareSubject(o1, o2);

  compareTeacher = (o1: ITeacher | null, o2: ITeacher | null): boolean => this.teacherService.compareTeacher(o1, o2);

  compareSchoolClass = (o1: ISchoolClass | null, o2: ISchoolClass | null): boolean => this.schoolClassService.compareSchoolClass(o1, o2);

  compareTimetable = (o1: ITimetable | null, o2: ITimetable | null): boolean => this.timetableService.compareTimetable(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ session }) => {
      this.session = session;
      if (session) {
        this.updateForm(session);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const session = this.sessionFormService.getSession(this.editForm);
    if (session.id !== null) {
      this.subscribeToSaveResponse(this.sessionService.update(session));
    } else {
      this.subscribeToSaveResponse(this.sessionService.create(session));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISession>>): void {
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

  protected updateForm(session: ISession): void {
    this.session = session;
    this.sessionFormService.resetForm(this.editForm, session);

    this.roomsSharedCollection = this.roomService.addRoomToCollectionIfMissing<IRoom>(this.roomsSharedCollection, session.room);
    this.subjectsSharedCollection = this.subjectService.addSubjectToCollectionIfMissing<ISubject>(
      this.subjectsSharedCollection,
      session.subject,
    );
    this.teachersSharedCollection = this.teacherService.addTeacherToCollectionIfMissing<ITeacher>(
      this.teachersSharedCollection,
      session.teacher,
    );
    this.schoolClassesSharedCollection = this.schoolClassService.addSchoolClassToCollectionIfMissing<ISchoolClass>(
      this.schoolClassesSharedCollection,
      session.schoolClass,
    );
    this.timetablesSharedCollection = this.timetableService.addTimetableToCollectionIfMissing<ITimetable>(
      this.timetablesSharedCollection,
      session.timetable,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.roomService
      .query()
      .pipe(map((res: HttpResponse<IRoom[]>) => res.body ?? []))
      .pipe(map((rooms: IRoom[]) => this.roomService.addRoomToCollectionIfMissing<IRoom>(rooms, this.session?.room)))
      .subscribe((rooms: IRoom[]) => (this.roomsSharedCollection = rooms));

    this.subjectService
      .query()
      .pipe(map((res: HttpResponse<ISubject[]>) => res.body ?? []))
      .pipe(map((subjects: ISubject[]) => this.subjectService.addSubjectToCollectionIfMissing<ISubject>(subjects, this.session?.subject)))
      .subscribe((subjects: ISubject[]) => (this.subjectsSharedCollection = subjects));

    this.teacherService
      .query()
      .pipe(map((res: HttpResponse<ITeacher[]>) => res.body ?? []))
      .pipe(map((teachers: ITeacher[]) => this.teacherService.addTeacherToCollectionIfMissing<ITeacher>(teachers, this.session?.teacher)))
      .subscribe((teachers: ITeacher[]) => (this.teachersSharedCollection = teachers));

    this.schoolClassService
      .query()
      .pipe(map((res: HttpResponse<ISchoolClass[]>) => res.body ?? []))
      .pipe(
        map((schoolClasses: ISchoolClass[]) =>
          this.schoolClassService.addSchoolClassToCollectionIfMissing<ISchoolClass>(schoolClasses, this.session?.schoolClass),
        ),
      )
      .subscribe((schoolClasses: ISchoolClass[]) => (this.schoolClassesSharedCollection = schoolClasses));

    this.timetableService
      .query()
      .pipe(map((res: HttpResponse<ITimetable[]>) => res.body ?? []))
      .pipe(
        map((timetables: ITimetable[]) =>
          this.timetableService.addTimetableToCollectionIfMissing<ITimetable>(timetables, this.session?.timetable),
        ),
      )
      .subscribe((timetables: ITimetable[]) => (this.timetablesSharedCollection = timetables));
  }
}
