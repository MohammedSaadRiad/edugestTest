import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IStudent } from 'app/entities/eduGestMicroservice/student/student.model';
import { StudentService } from 'app/entities/eduGestMicroservice/student/service/student.service';
import { ISession } from 'app/entities/eduGestMicroservice/session/session.model';
import { SessionService } from 'app/entities/eduGestMicroservice/session/service/session.service';
import { IAbsence } from '../absence.model';
import { AbsenceService } from '../service/absence.service';
import { AbsenceFormService } from './absence-form.service';

import { AbsenceUpdateComponent } from './absence-update.component';

describe('Absence Management Update Component', () => {
  let comp: AbsenceUpdateComponent;
  let fixture: ComponentFixture<AbsenceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let absenceFormService: AbsenceFormService;
  let absenceService: AbsenceService;
  let studentService: StudentService;
  let sessionService: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AbsenceUpdateComponent],
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
      .overrideTemplate(AbsenceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AbsenceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    absenceFormService = TestBed.inject(AbsenceFormService);
    absenceService = TestBed.inject(AbsenceService);
    studentService = TestBed.inject(StudentService);
    sessionService = TestBed.inject(SessionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Student query and add missing value', () => {
      const absence: IAbsence = { id: 9045 };
      const student: IStudent = { id: 9978 };
      absence.student = student;

      const studentCollection: IStudent[] = [{ id: 9978 }];
      jest.spyOn(studentService, 'query').mockReturnValue(of(new HttpResponse({ body: studentCollection })));
      const additionalStudents = [student];
      const expectedCollection: IStudent[] = [...additionalStudents, ...studentCollection];
      jest.spyOn(studentService, 'addStudentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ absence });
      comp.ngOnInit();

      expect(studentService.query).toHaveBeenCalled();
      expect(studentService.addStudentToCollectionIfMissing).toHaveBeenCalledWith(
        studentCollection,
        ...additionalStudents.map(expect.objectContaining),
      );
      expect(comp.studentsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Session query and add missing value', () => {
      const absence: IAbsence = { id: 9045 };
      const session: ISession = { id: 29041 };
      absence.session = session;

      const sessionCollection: ISession[] = [{ id: 29041 }];
      jest.spyOn(sessionService, 'query').mockReturnValue(of(new HttpResponse({ body: sessionCollection })));
      const additionalSessions = [session];
      const expectedCollection: ISession[] = [...additionalSessions, ...sessionCollection];
      jest.spyOn(sessionService, 'addSessionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ absence });
      comp.ngOnInit();

      expect(sessionService.query).toHaveBeenCalled();
      expect(sessionService.addSessionToCollectionIfMissing).toHaveBeenCalledWith(
        sessionCollection,
        ...additionalSessions.map(expect.objectContaining),
      );
      expect(comp.sessionsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const absence: IAbsence = { id: 9045 };
      const student: IStudent = { id: 9978 };
      absence.student = student;
      const session: ISession = { id: 29041 };
      absence.session = session;

      activatedRoute.data = of({ absence });
      comp.ngOnInit();

      expect(comp.studentsSharedCollection).toContainEqual(student);
      expect(comp.sessionsSharedCollection).toContainEqual(session);
      expect(comp.absence).toEqual(absence);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAbsence>>();
      const absence = { id: 3675 };
      jest.spyOn(absenceFormService, 'getAbsence').mockReturnValue(absence);
      jest.spyOn(absenceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ absence });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: absence }));
      saveSubject.complete();

      // THEN
      expect(absenceFormService.getAbsence).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(absenceService.update).toHaveBeenCalledWith(expect.objectContaining(absence));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAbsence>>();
      const absence = { id: 3675 };
      jest.spyOn(absenceFormService, 'getAbsence').mockReturnValue({ id: null });
      jest.spyOn(absenceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ absence: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: absence }));
      saveSubject.complete();

      // THEN
      expect(absenceFormService.getAbsence).toHaveBeenCalled();
      expect(absenceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAbsence>>();
      const absence = { id: 3675 };
      jest.spyOn(absenceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ absence });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(absenceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareStudent', () => {
      it('should forward to studentService', () => {
        const entity = { id: 9978 };
        const entity2 = { id: 22718 };
        jest.spyOn(studentService, 'compareStudent');
        comp.compareStudent(entity, entity2);
        expect(studentService.compareStudent).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSession', () => {
      it('should forward to sessionService', () => {
        const entity = { id: 29041 };
        const entity2 = { id: 8543 };
        jest.spyOn(sessionService, 'compareSession');
        comp.compareSession(entity, entity2);
        expect(sessionService.compareSession).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
