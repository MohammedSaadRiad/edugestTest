import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ISchoolClass } from 'app/entities/eduGestMicroservice/school-class/school-class.model';
import { SchoolClassService } from 'app/entities/eduGestMicroservice/school-class/service/school-class.service';
import { IParent } from 'app/entities/eduGestMicroservice/parent/parent.model';
import { ParentService } from 'app/entities/eduGestMicroservice/parent/service/parent.service';
import { IStudent } from '../student.model';
import { StudentService } from '../service/student.service';
import { StudentFormService } from './student-form.service';

import { StudentUpdateComponent } from './student-update.component';

describe('Student Management Update Component', () => {
  let comp: StudentUpdateComponent;
  let fixture: ComponentFixture<StudentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let studentFormService: StudentFormService;
  let studentService: StudentService;
  let schoolClassService: SchoolClassService;
  let parentService: ParentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StudentUpdateComponent],
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
      .overrideTemplate(StudentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StudentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    studentFormService = TestBed.inject(StudentFormService);
    studentService = TestBed.inject(StudentService);
    schoolClassService = TestBed.inject(SchoolClassService);
    parentService = TestBed.inject(ParentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call SchoolClass query and add missing value', () => {
      const student: IStudent = { id: 22718 };
      const schoolClass: ISchoolClass = { id: 21619 };
      student.schoolClass = schoolClass;

      const schoolClassCollection: ISchoolClass[] = [{ id: 21619 }];
      jest.spyOn(schoolClassService, 'query').mockReturnValue(of(new HttpResponse({ body: schoolClassCollection })));
      const additionalSchoolClasses = [schoolClass];
      const expectedCollection: ISchoolClass[] = [...additionalSchoolClasses, ...schoolClassCollection];
      jest.spyOn(schoolClassService, 'addSchoolClassToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ student });
      comp.ngOnInit();

      expect(schoolClassService.query).toHaveBeenCalled();
      expect(schoolClassService.addSchoolClassToCollectionIfMissing).toHaveBeenCalledWith(
        schoolClassCollection,
        ...additionalSchoolClasses.map(expect.objectContaining),
      );
      expect(comp.schoolClassesSharedCollection).toEqual(expectedCollection);
    });

    it('should call Parent query and add missing value', () => {
      const student: IStudent = { id: 22718 };
      const parents: IParent[] = [{ id: 10134 }];
      student.parents = parents;

      const parentCollection: IParent[] = [{ id: 10134 }];
      jest.spyOn(parentService, 'query').mockReturnValue(of(new HttpResponse({ body: parentCollection })));
      const additionalParents = [...parents];
      const expectedCollection: IParent[] = [...additionalParents, ...parentCollection];
      jest.spyOn(parentService, 'addParentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ student });
      comp.ngOnInit();

      expect(parentService.query).toHaveBeenCalled();
      expect(parentService.addParentToCollectionIfMissing).toHaveBeenCalledWith(
        parentCollection,
        ...additionalParents.map(expect.objectContaining),
      );
      expect(comp.parentsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const student: IStudent = { id: 22718 };
      const schoolClass: ISchoolClass = { id: 21619 };
      student.schoolClass = schoolClass;
      const parents: IParent = { id: 10134 };
      student.parents = [parents];

      activatedRoute.data = of({ student });
      comp.ngOnInit();

      expect(comp.schoolClassesSharedCollection).toContainEqual(schoolClass);
      expect(comp.parentsSharedCollection).toContainEqual(parents);
      expect(comp.student).toEqual(student);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStudent>>();
      const student = { id: 9978 };
      jest.spyOn(studentFormService, 'getStudent').mockReturnValue(student);
      jest.spyOn(studentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ student });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: student }));
      saveSubject.complete();

      // THEN
      expect(studentFormService.getStudent).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(studentService.update).toHaveBeenCalledWith(expect.objectContaining(student));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStudent>>();
      const student = { id: 9978 };
      jest.spyOn(studentFormService, 'getStudent').mockReturnValue({ id: null });
      jest.spyOn(studentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ student: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: student }));
      saveSubject.complete();

      // THEN
      expect(studentFormService.getStudent).toHaveBeenCalled();
      expect(studentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStudent>>();
      const student = { id: 9978 };
      jest.spyOn(studentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ student });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(studentService.update).toHaveBeenCalled();
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

    describe('compareParent', () => {
      it('should forward to parentService', () => {
        const entity = { id: 10134 };
        const entity2 = { id: 2353 };
        jest.spyOn(parentService, 'compareParent');
        comp.compareParent(entity, entity2);
        expect(parentService.compareParent).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
