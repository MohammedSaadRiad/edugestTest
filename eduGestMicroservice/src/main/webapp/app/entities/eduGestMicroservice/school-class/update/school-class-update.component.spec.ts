import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITimetable } from 'app/entities/eduGestMicroservice/timetable/timetable.model';
import { TimetableService } from 'app/entities/eduGestMicroservice/timetable/service/timetable.service';
import { ITeacher } from 'app/entities/eduGestMicroservice/teacher/teacher.model';
import { TeacherService } from 'app/entities/eduGestMicroservice/teacher/service/teacher.service';
import { ISubject } from 'app/entities/eduGestMicroservice/subject/subject.model';
import { SubjectService } from 'app/entities/eduGestMicroservice/subject/service/subject.service';
import { ISchoolClass } from '../school-class.model';
import { SchoolClassService } from '../service/school-class.service';
import { SchoolClassFormService } from './school-class-form.service';

import { SchoolClassUpdateComponent } from './school-class-update.component';

describe('SchoolClass Management Update Component', () => {
  let comp: SchoolClassUpdateComponent;
  let fixture: ComponentFixture<SchoolClassUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let schoolClassFormService: SchoolClassFormService;
  let schoolClassService: SchoolClassService;
  let timetableService: TimetableService;
  let teacherService: TeacherService;
  let subjectService: SubjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SchoolClassUpdateComponent],
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
      .overrideTemplate(SchoolClassUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SchoolClassUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    schoolClassFormService = TestBed.inject(SchoolClassFormService);
    schoolClassService = TestBed.inject(SchoolClassService);
    timetableService = TestBed.inject(TimetableService);
    teacherService = TestBed.inject(TeacherService);
    subjectService = TestBed.inject(SubjectService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call timetable query and add missing value', () => {
      const schoolClass: ISchoolClass = { id: 18628 };
      const timetable: ITimetable = { id: 31934 };
      schoolClass.timetable = timetable;

      const timetableCollection: ITimetable[] = [{ id: 31934 }];
      jest.spyOn(timetableService, 'query').mockReturnValue(of(new HttpResponse({ body: timetableCollection })));
      const expectedCollection: ITimetable[] = [timetable, ...timetableCollection];
      jest.spyOn(timetableService, 'addTimetableToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ schoolClass });
      comp.ngOnInit();

      expect(timetableService.query).toHaveBeenCalled();
      expect(timetableService.addTimetableToCollectionIfMissing).toHaveBeenCalledWith(timetableCollection, timetable);
      expect(comp.timetablesCollection).toEqual(expectedCollection);
    });

    it('should call Teacher query and add missing value', () => {
      const schoolClass: ISchoolClass = { id: 18628 };
      const teachers: ITeacher[] = [{ id: 11312 }];
      schoolClass.teachers = teachers;

      const teacherCollection: ITeacher[] = [{ id: 11312 }];
      jest.spyOn(teacherService, 'query').mockReturnValue(of(new HttpResponse({ body: teacherCollection })));
      const additionalTeachers = [...teachers];
      const expectedCollection: ITeacher[] = [...additionalTeachers, ...teacherCollection];
      jest.spyOn(teacherService, 'addTeacherToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ schoolClass });
      comp.ngOnInit();

      expect(teacherService.query).toHaveBeenCalled();
      expect(teacherService.addTeacherToCollectionIfMissing).toHaveBeenCalledWith(
        teacherCollection,
        ...additionalTeachers.map(expect.objectContaining),
      );
      expect(comp.teachersSharedCollection).toEqual(expectedCollection);
    });

    it('should call Subject query and add missing value', () => {
      const schoolClass: ISchoolClass = { id: 18628 };
      const subjects: ISubject[] = [{ id: 16494 }];
      schoolClass.subjects = subjects;

      const subjectCollection: ISubject[] = [{ id: 16494 }];
      jest.spyOn(subjectService, 'query').mockReturnValue(of(new HttpResponse({ body: subjectCollection })));
      const additionalSubjects = [...subjects];
      const expectedCollection: ISubject[] = [...additionalSubjects, ...subjectCollection];
      jest.spyOn(subjectService, 'addSubjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ schoolClass });
      comp.ngOnInit();

      expect(subjectService.query).toHaveBeenCalled();
      expect(subjectService.addSubjectToCollectionIfMissing).toHaveBeenCalledWith(
        subjectCollection,
        ...additionalSubjects.map(expect.objectContaining),
      );
      expect(comp.subjectsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const schoolClass: ISchoolClass = { id: 18628 };
      const timetable: ITimetable = { id: 31934 };
      schoolClass.timetable = timetable;
      const teachers: ITeacher = { id: 11312 };
      schoolClass.teachers = [teachers];
      const subjects: ISubject = { id: 16494 };
      schoolClass.subjects = [subjects];

      activatedRoute.data = of({ schoolClass });
      comp.ngOnInit();

      expect(comp.timetablesCollection).toContainEqual(timetable);
      expect(comp.teachersSharedCollection).toContainEqual(teachers);
      expect(comp.subjectsSharedCollection).toContainEqual(subjects);
      expect(comp.schoolClass).toEqual(schoolClass);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISchoolClass>>();
      const schoolClass = { id: 21619 };
      jest.spyOn(schoolClassFormService, 'getSchoolClass').mockReturnValue(schoolClass);
      jest.spyOn(schoolClassService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ schoolClass });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: schoolClass }));
      saveSubject.complete();

      // THEN
      expect(schoolClassFormService.getSchoolClass).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(schoolClassService.update).toHaveBeenCalledWith(expect.objectContaining(schoolClass));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISchoolClass>>();
      const schoolClass = { id: 21619 };
      jest.spyOn(schoolClassFormService, 'getSchoolClass').mockReturnValue({ id: null });
      jest.spyOn(schoolClassService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ schoolClass: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: schoolClass }));
      saveSubject.complete();

      // THEN
      expect(schoolClassFormService.getSchoolClass).toHaveBeenCalled();
      expect(schoolClassService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISchoolClass>>();
      const schoolClass = { id: 21619 };
      jest.spyOn(schoolClassService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ schoolClass });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(schoolClassService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTimetable', () => {
      it('should forward to timetableService', () => {
        const entity = { id: 31934 };
        const entity2 = { id: 13392 };
        jest.spyOn(timetableService, 'compareTimetable');
        comp.compareTimetable(entity, entity2);
        expect(timetableService.compareTimetable).toHaveBeenCalledWith(entity, entity2);
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
