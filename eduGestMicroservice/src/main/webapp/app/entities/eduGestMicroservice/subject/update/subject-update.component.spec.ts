import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ISchoolClass } from 'app/entities/eduGestMicroservice/school-class/school-class.model';
import { SchoolClassService } from 'app/entities/eduGestMicroservice/school-class/service/school-class.service';
import { ITeacher } from 'app/entities/eduGestMicroservice/teacher/teacher.model';
import { TeacherService } from 'app/entities/eduGestMicroservice/teacher/service/teacher.service';
import { ISubject } from '../subject.model';
import { SubjectService } from '../service/subject.service';
import { SubjectFormService } from './subject-form.service';

import { SubjectUpdateComponent } from './subject-update.component';

describe('Subject Management Update Component', () => {
  let comp: SubjectUpdateComponent;
  let fixture: ComponentFixture<SubjectUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let subjectFormService: SubjectFormService;
  let subjectService: SubjectService;
  let schoolClassService: SchoolClassService;
  let teacherService: TeacherService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SubjectUpdateComponent],
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
      .overrideTemplate(SubjectUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SubjectUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    subjectFormService = TestBed.inject(SubjectFormService);
    subjectService = TestBed.inject(SubjectService);
    schoolClassService = TestBed.inject(SchoolClassService);
    teacherService = TestBed.inject(TeacherService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call SchoolClass query and add missing value', () => {
      const subject: ISubject = { id: 11747 };
      const schoolClasses: ISchoolClass[] = [{ id: 21619 }];
      subject.schoolClasses = schoolClasses;

      const schoolClassCollection: ISchoolClass[] = [{ id: 21619 }];
      jest.spyOn(schoolClassService, 'query').mockReturnValue(of(new HttpResponse({ body: schoolClassCollection })));
      const additionalSchoolClasses = [...schoolClasses];
      const expectedCollection: ISchoolClass[] = [...additionalSchoolClasses, ...schoolClassCollection];
      jest.spyOn(schoolClassService, 'addSchoolClassToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ subject });
      comp.ngOnInit();

      expect(schoolClassService.query).toHaveBeenCalled();
      expect(schoolClassService.addSchoolClassToCollectionIfMissing).toHaveBeenCalledWith(
        schoolClassCollection,
        ...additionalSchoolClasses.map(expect.objectContaining),
      );
      expect(comp.schoolClassesSharedCollection).toEqual(expectedCollection);
    });

    it('should call Teacher query and add missing value', () => {
      const subject: ISubject = { id: 11747 };
      const teachers: ITeacher[] = [{ id: 11312 }];
      subject.teachers = teachers;

      const teacherCollection: ITeacher[] = [{ id: 11312 }];
      jest.spyOn(teacherService, 'query').mockReturnValue(of(new HttpResponse({ body: teacherCollection })));
      const additionalTeachers = [...teachers];
      const expectedCollection: ITeacher[] = [...additionalTeachers, ...teacherCollection];
      jest.spyOn(teacherService, 'addTeacherToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ subject });
      comp.ngOnInit();

      expect(teacherService.query).toHaveBeenCalled();
      expect(teacherService.addTeacherToCollectionIfMissing).toHaveBeenCalledWith(
        teacherCollection,
        ...additionalTeachers.map(expect.objectContaining),
      );
      expect(comp.teachersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const subject: ISubject = { id: 11747 };
      const schoolClasses: ISchoolClass = { id: 21619 };
      subject.schoolClasses = [schoolClasses];
      const teachers: ITeacher = { id: 11312 };
      subject.teachers = [teachers];

      activatedRoute.data = of({ subject });
      comp.ngOnInit();

      expect(comp.schoolClassesSharedCollection).toContainEqual(schoolClasses);
      expect(comp.teachersSharedCollection).toContainEqual(teachers);
      expect(comp.subject).toEqual(subject);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubject>>();
      const subject = { id: 16494 };
      jest.spyOn(subjectFormService, 'getSubject').mockReturnValue(subject);
      jest.spyOn(subjectService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subject });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: subject }));
      saveSubject.complete();

      // THEN
      expect(subjectFormService.getSubject).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(subjectService.update).toHaveBeenCalledWith(expect.objectContaining(subject));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubject>>();
      const subject = { id: 16494 };
      jest.spyOn(subjectFormService, 'getSubject').mockReturnValue({ id: null });
      jest.spyOn(subjectService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subject: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: subject }));
      saveSubject.complete();

      // THEN
      expect(subjectFormService.getSubject).toHaveBeenCalled();
      expect(subjectService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubject>>();
      const subject = { id: 16494 };
      jest.spyOn(subjectService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subject });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(subjectService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSchoolClass', () => {
      it('should forward to schoolClassService', () => {
        const entity = { id: 21619 };
        const entity2 = { id: 18628 };
        jest.spyOn(schoolClassService, 'compareSchoolClass');
        comp.compareSchoolClass(entity, entity2);
        expect(schoolClassService.compareSchoolClass).toHaveBeenCalledWith(entity, entity2);
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
  });
});
