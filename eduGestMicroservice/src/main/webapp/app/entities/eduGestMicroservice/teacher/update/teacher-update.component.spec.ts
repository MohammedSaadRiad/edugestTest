import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ISubject } from 'app/entities/eduGestMicroservice/subject/subject.model';
import { SubjectService } from 'app/entities/eduGestMicroservice/subject/service/subject.service';
import { ISchoolClass } from 'app/entities/eduGestMicroservice/school-class/school-class.model';
import { SchoolClassService } from 'app/entities/eduGestMicroservice/school-class/service/school-class.service';
import { ITeacher } from '../teacher.model';
import { TeacherService } from '../service/teacher.service';
import { TeacherFormService } from './teacher-form.service';

import { TeacherUpdateComponent } from './teacher-update.component';

describe('Teacher Management Update Component', () => {
  let comp: TeacherUpdateComponent;
  let fixture: ComponentFixture<TeacherUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teacherFormService: TeacherFormService;
  let teacherService: TeacherService;
  let subjectService: SubjectService;
  let schoolClassService: SchoolClassService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TeacherUpdateComponent],
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
      .overrideTemplate(TeacherUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeacherUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teacherFormService = TestBed.inject(TeacherFormService);
    teacherService = TestBed.inject(TeacherService);
    subjectService = TestBed.inject(SubjectService);
    schoolClassService = TestBed.inject(SchoolClassService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Subject query and add missing value', () => {
      const teacher: ITeacher = { id: 13207 };
      const subjects: ISubject[] = [{ id: 16494 }];
      teacher.subjects = subjects;

      const subjectCollection: ISubject[] = [{ id: 16494 }];
      jest.spyOn(subjectService, 'query').mockReturnValue(of(new HttpResponse({ body: subjectCollection })));
      const additionalSubjects = [...subjects];
      const expectedCollection: ISubject[] = [...additionalSubjects, ...subjectCollection];
      jest.spyOn(subjectService, 'addSubjectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      expect(subjectService.query).toHaveBeenCalled();
      expect(subjectService.addSubjectToCollectionIfMissing).toHaveBeenCalledWith(
        subjectCollection,
        ...additionalSubjects.map(expect.objectContaining),
      );
      expect(comp.subjectsSharedCollection).toEqual(expectedCollection);
    });

    it('should call SchoolClass query and add missing value', () => {
      const teacher: ITeacher = { id: 13207 };
      const schoolClasses: ISchoolClass[] = [{ id: 21619 }];
      teacher.schoolClasses = schoolClasses;

      const schoolClassCollection: ISchoolClass[] = [{ id: 21619 }];
      jest.spyOn(schoolClassService, 'query').mockReturnValue(of(new HttpResponse({ body: schoolClassCollection })));
      const additionalSchoolClasses = [...schoolClasses];
      const expectedCollection: ISchoolClass[] = [...additionalSchoolClasses, ...schoolClassCollection];
      jest.spyOn(schoolClassService, 'addSchoolClassToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      expect(schoolClassService.query).toHaveBeenCalled();
      expect(schoolClassService.addSchoolClassToCollectionIfMissing).toHaveBeenCalledWith(
        schoolClassCollection,
        ...additionalSchoolClasses.map(expect.objectContaining),
      );
      expect(comp.schoolClassesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const teacher: ITeacher = { id: 13207 };
      const subjects: ISubject = { id: 16494 };
      teacher.subjects = [subjects];
      const schoolClasses: ISchoolClass = { id: 21619 };
      teacher.schoolClasses = [schoolClasses];

      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      expect(comp.subjectsSharedCollection).toContainEqual(subjects);
      expect(comp.schoolClassesSharedCollection).toContainEqual(schoolClasses);
      expect(comp.teacher).toEqual(teacher);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeacher>>();
      const teacher = { id: 11312 };
      jest.spyOn(teacherFormService, 'getTeacher').mockReturnValue(teacher);
      jest.spyOn(teacherService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teacher }));
      saveSubject.complete();

      // THEN
      expect(teacherFormService.getTeacher).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(teacherService.update).toHaveBeenCalledWith(expect.objectContaining(teacher));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeacher>>();
      const teacher = { id: 11312 };
      jest.spyOn(teacherFormService, 'getTeacher').mockReturnValue({ id: null });
      jest.spyOn(teacherService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teacher: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teacher }));
      saveSubject.complete();

      // THEN
      expect(teacherFormService.getTeacher).toHaveBeenCalled();
      expect(teacherService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeacher>>();
      const teacher = { id: 11312 };
      jest.spyOn(teacherService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teacher });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teacherService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSubject', () => {
      it('should forward to subjectService', () => {
        const entity = { id: 16494 };
        const entity2 = { id: 11747 };
        jest.spyOn(subjectService, 'compareSubject');
        comp.compareSubject(entity, entity2);
        expect(subjectService.compareSubject).toHaveBeenCalledWith(entity, entity2);
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
  });
});
