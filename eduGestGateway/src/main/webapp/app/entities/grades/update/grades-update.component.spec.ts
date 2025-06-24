import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IStudent } from 'app/entities/student/student.model';
import { StudentService } from 'app/entities/student/service/student.service';
import { ISubject } from 'app/entities/subject/subject.model';
import { SubjectService } from 'app/entities/subject/service/subject.service';
import { IGrades } from '../grades.model';
import { GradesService } from '../service/grades.service';
import { GradesFormService } from './grades-form.service';

import { GradesUpdateComponent } from './grades-update.component';

describe('Grades Management Update Component', () => {
  let comp: GradesUpdateComponent;
  let fixture: ComponentFixture<GradesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let gradesFormService: GradesFormService;
  let gradesService: GradesService;
  let studentService: StudentService;
  let subjectService: SubjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [GradesUpdateComponent],
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
      .overrideTemplate(GradesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GradesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gradesFormService = TestBed.inject(GradesFormService);
    gradesService = TestBed.inject(GradesService);
    studentService = TestBed.inject(StudentService);
    subjectService = TestBed.inject(SubjectService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Student query and add missing value', () => {
      const grades: IGrades = { id: 26965 };
      const student: IStudent = { id: 9978 };
      grades.student = student;

      const studentCollection: IStudent[] = [{ id: 9978 }];
      jest.spyOn(studentService, 'query').mockReturnValue(of(new HttpResponse({ body: studentCollection })));
      const additionalStudents = [student];
      const expectedCollection: IStudent[] = [...additionalStudents, ...studentCollection];
      jest.spyOn(studentService, 'addStudentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ grades });
      comp.ngOnInit();

      expect(studentService.query).toHaveBeenCalled();
      expect(studentService.addStudentToCollectionIfMissing).toHaveBeenCalledWith(
        studentCollection,
        ...additionalStudents.map(expect.objectContaining),
      );
      expect(comp.studentsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Subject query and add missing value', () => {
      const grades: IGrades = { id: 26965 };
      const subject: ISubject = { id: 16494 };
      grades.subject = subject;

      const subjectCollection: ISubject[] = [{ id: 16494 }];
      jest.spyOn(subjectService, 'query').mockReturnValue(of(new HttpResponse({ body: subjectCollection })));
      const additionalSubjects = [subject];
      const expectedCollection: ISubject[] = [...additionalSubjects, ...subjectCollection];
      jest.spyOn(subjectService, 'addSubjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ grades });
      comp.ngOnInit();

      expect(subjectService.query).toHaveBeenCalled();
      expect(subjectService.addSubjectToCollectionIfMissing).toHaveBeenCalledWith(
        subjectCollection,
        ...additionalSubjects.map(expect.objectContaining),
      );
      expect(comp.subjectsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const grades: IGrades = { id: 26965 };
      const student: IStudent = { id: 9978 };
      grades.student = student;
      const subject: ISubject = { id: 16494 };
      grades.subject = subject;

      activatedRoute.data = of({ grades });
      comp.ngOnInit();

      expect(comp.studentsSharedCollection).toContainEqual(student);
      expect(comp.subjectsSharedCollection).toContainEqual(subject);
      expect(comp.grades).toEqual(grades);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGrades>>();
      const grades = { id: 25513 };
      jest.spyOn(gradesFormService, 'getGrades').mockReturnValue(grades);
      jest.spyOn(gradesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ grades });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: grades }));
      saveSubject.complete();

      // THEN
      expect(gradesFormService.getGrades).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(gradesService.update).toHaveBeenCalledWith(expect.objectContaining(grades));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGrades>>();
      const grades = { id: 25513 };
      jest.spyOn(gradesFormService, 'getGrades').mockReturnValue({ id: null });
      jest.spyOn(gradesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ grades: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: grades }));
      saveSubject.complete();

      // THEN
      expect(gradesFormService.getGrades).toHaveBeenCalled();
      expect(gradesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGrades>>();
      const grades = { id: 25513 };
      jest.spyOn(gradesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ grades });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gradesService.update).toHaveBeenCalled();
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

    describe('compareSubject', () => {
      it('should forward to subjectService', () => {
        const entity = { id: 16494 };
        const entity2 = { id: 11747 };
        jest.spyOn(subjectService, 'compareSubject');
        comp.compareSubject(entity, entity2);
        expect(subjectService.compareSubject).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
