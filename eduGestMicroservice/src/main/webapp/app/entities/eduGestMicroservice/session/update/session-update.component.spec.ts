import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IRoom } from 'app/entities/eduGestMicroservice/room/room.model';
import { RoomService } from 'app/entities/eduGestMicroservice/room/service/room.service';
import { ISubject } from 'app/entities/eduGestMicroservice/subject/subject.model';
import { SubjectService } from 'app/entities/eduGestMicroservice/subject/service/subject.service';
import { ITeacher } from 'app/entities/eduGestMicroservice/teacher/teacher.model';
import { TeacherService } from 'app/entities/eduGestMicroservice/teacher/service/teacher.service';
import { ISchoolClass } from 'app/entities/eduGestMicroservice/school-class/school-class.model';
import { SchoolClassService } from 'app/entities/eduGestMicroservice/school-class/service/school-class.service';
import { ITimetable } from 'app/entities/eduGestMicroservice/timetable/timetable.model';
import { TimetableService } from 'app/entities/eduGestMicroservice/timetable/service/timetable.service';
import { ISession } from '../session.model';
import { SessionService } from '../service/session.service';
import { SessionFormService } from './session-form.service';

import { SessionUpdateComponent } from './session-update.component';

describe('Session Management Update Component', () => {
  let comp: SessionUpdateComponent;
  let fixture: ComponentFixture<SessionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sessionFormService: SessionFormService;
  let sessionService: SessionService;
  let roomService: RoomService;
  let subjectService: SubjectService;
  let teacherService: TeacherService;
  let schoolClassService: SchoolClassService;
  let timetableService: TimetableService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SessionUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SessionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SessionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sessionFormService = TestBed.inject(SessionFormService);
    sessionService = TestBed.inject(SessionService);
    roomService = TestBed.inject(RoomService);
    subjectService = TestBed.inject(SubjectService);
    teacherService = TestBed.inject(TeacherService);
    schoolClassService = TestBed.inject(SchoolClassService);
    timetableService = TestBed.inject(TimetableService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Room query and add missing value', () => {
      const session: ISession = { id: 8543 };
      const room: IRoom = { id: 31469 };
      session.room = room;

      const roomCollection: IRoom[] = [{ id: 31469 }];
      jest.spyOn(roomService, 'query').mockReturnValue(of(new HttpResponse({ body: roomCollection })));
      const additionalRooms = [room];
      const expectedCollection: IRoom[] = [...additionalRooms, ...roomCollection];
      jest.spyOn(roomService, 'addRoomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(roomService.query).toHaveBeenCalled();
      expect(roomService.addRoomToCollectionIfMissing).toHaveBeenCalledWith(
        roomCollection,
        ...additionalRooms.map(expect.objectContaining),
      );
      expect(comp.roomsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Subject query and add missing value', () => {
      const session: ISession = { id: 8543 };
      const subject: ISubject = { id: 16494 };
      session.subject = subject;

      const subjectCollection: ISubject[] = [{ id: 16494 }];
      jest.spyOn(subjectService, 'query').mockReturnValue(of(new HttpResponse({ body: subjectCollection })));
      const additionalSubjects = [subject];
      const expectedCollection: ISubject[] = [...additionalSubjects, ...subjectCollection];
      jest.spyOn(subjectService, 'addSubjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(subjectService.query).toHaveBeenCalled();
      expect(subjectService.addSubjectToCollectionIfMissing).toHaveBeenCalledWith(
        subjectCollection,
        ...additionalSubjects.map(expect.objectContaining),
      );
      expect(comp.subjectsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Teacher query and add missing value', () => {
      const session: ISession = { id: 8543 };
      const teacher: ITeacher = { id: 11312 };
      session.teacher = teacher;

      const teacherCollection: ITeacher[] = [{ id: 11312 }];
      jest.spyOn(teacherService, 'query').mockReturnValue(of(new HttpResponse({ body: teacherCollection })));
      const additionalTeachers = [teacher];
      const expectedCollection: ITeacher[] = [...additionalTeachers, ...teacherCollection];
      jest.spyOn(teacherService, 'addTeacherToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(teacherService.query).toHaveBeenCalled();
      expect(teacherService.addTeacherToCollectionIfMissing).toHaveBeenCalledWith(
        teacherCollection,
        ...additionalTeachers.map(expect.objectContaining),
      );
      expect(comp.teachersSharedCollection).toEqual(expectedCollection);
    });

    it('should call SchoolClass query and add missing value', () => {
      const session: ISession = { id: 8543 };
      const schoolClass: ISchoolClass = { id: 21619 };
      session.schoolClass = schoolClass;

      const schoolClassCollection: ISchoolClass[] = [{ id: 21619 }];
      jest.spyOn(schoolClassService, 'query').mockReturnValue(of(new HttpResponse({ body: schoolClassCollection })));
      const additionalSchoolClasses = [schoolClass];
      const expectedCollection: ISchoolClass[] = [...additionalSchoolClasses, ...schoolClassCollection];
      jest.spyOn(schoolClassService, 'addSchoolClassToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(schoolClassService.query).toHaveBeenCalled();
      expect(schoolClassService.addSchoolClassToCollectionIfMissing).toHaveBeenCalledWith(
        schoolClassCollection,
        ...additionalSchoolClasses.map(expect.objectContaining),
      );
      expect(comp.schoolClassesSharedCollection).toEqual(expectedCollection);
    });

    it('should call Timetable query and add missing value', () => {
      const session: ISession = { id: 8543 };
      const timetable: ITimetable = { id: 31934 };
      session.timetable = timetable;

      const timetableCollection: ITimetable[] = [{ id: 31934 }];
      jest.spyOn(timetableService, 'query').mockReturnValue(of(new HttpResponse({ body: timetableCollection })));
      const additionalTimetables = [timetable];
      const expectedCollection: ITimetable[] = [...additionalTimetables, ...timetableCollection];
      jest.spyOn(timetableService, 'addTimetableToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(timetableService.query).toHaveBeenCalled();
      expect(timetableService.addTimetableToCollectionIfMissing).toHaveBeenCalledWith(
        timetableCollection,
        ...additionalTimetables.map(expect.objectContaining),
      );
      expect(comp.timetablesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const session: ISession = { id: 8543 };
      const room: IRoom = { id: 31469 };
      session.room = room;
      const subject: ISubject = { id: 16494 };
      session.subject = subject;
      const teacher: ITeacher = { id: 11312 };
      session.teacher = teacher;
      const schoolClass: ISchoolClass = { id: 21619 };
      session.schoolClass = schoolClass;
      const timetable: ITimetable = { id: 31934 };
      session.timetable = timetable;

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(comp.roomsSharedCollection).toContainEqual(room);
      expect(comp.subjectsSharedCollection).toContainEqual(subject);
      expect(comp.teachersSharedCollection).toContainEqual(teacher);
      expect(comp.schoolClassesSharedCollection).toContainEqual(schoolClass);
      expect(comp.timetablesSharedCollection).toContainEqual(timetable);
      expect(comp.session).toEqual(session);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISession>>();
      const session = { id: 29041 };
      jest.spyOn(sessionFormService, 'getSession').mockReturnValue(session);
      jest.spyOn(sessionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ session });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: session }));
      saveSubject.complete();

      // THEN
      expect(sessionFormService.getSession).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sessionService.update).toHaveBeenCalledWith(expect.objectContaining(session));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISession>>();
      const session = { id: 29041 };
      jest.spyOn(sessionFormService, 'getSession').mockReturnValue({ id: null });
      jest.spyOn(sessionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ session: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: session }));
      saveSubject.complete();

      // THEN
      expect(sessionFormService.getSession).toHaveBeenCalled();
      expect(sessionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISession>>();
      const session = { id: 29041 };
      jest.spyOn(sessionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ session });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sessionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRoom', () => {
      it('should forward to roomService', () => {
        const entity = { id: 31469 };
        const entity2 = { id: 22394 };
        jest.spyOn(roomService, 'compareRoom');
        comp.compareRoom(entity, entity2);
        expect(roomService.compareRoom).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSubject', () => {
      it('should forward to subjectService', () => {
        const entity = { id: 16494 };
        const entity2 = { id: 11747 };
        jest.spyOn(subjectService, 'compareSubject');
        comp.compareSubject(entity, entity2);
        expect(subjectService.compareSubject).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTeacher', () => {
      it('should forward to teacherService', () => {
        const entity = { id: 11312 };
        const entity2 = { id: 13207 };
        jest.spyOn(teacherService, 'compareTeacher');
        comp.compareTeacher(entity, entity2);
        expect(teacherService.compareTeacher).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSchoolClass', () => {
      it('should forward to schoolClassService', () => {
        const entity = { id: 21619 };
        const entity2 = { id: 18628 };
        jest.spyOn(schoolClassService, 'compareSchoolClass');
        comp.compareSchoolClass(entity, entity2);
        expect(schoolClassService.compareSchoolClass).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTimetable', () => {
      it('should forward to timetableService', () => {
        const entity = { id: 31934 };
        const entity2 = { id: 13392 };
        jest.spyOn(timetableService, 'compareTimetable');
        comp.compareTimetable(entity, entity2);
        expect(timetableService.compareTimetable).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
