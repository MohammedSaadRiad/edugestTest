import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IStudent } from 'app/entities/student/student.model';
import { StudentService } from 'app/entities/student/service/student.service';
import { ParentService } from '../service/parent.service';
import { IParent } from '../parent.model';
import { ParentFormService } from './parent-form.service';

import { ParentUpdateComponent } from './parent-update.component';

describe('Parent Management Update Component', () => {
  let comp: ParentUpdateComponent;
  let fixture: ComponentFixture<ParentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let parentFormService: ParentFormService;
  let parentService: ParentService;
  let studentService: StudentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ParentUpdateComponent],
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
      .overrideTemplate(ParentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ParentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    parentFormService = TestBed.inject(ParentFormService);
    parentService = TestBed.inject(ParentService);
    studentService = TestBed.inject(StudentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Student query and add missing value', () => {
      const parent: IParent = { id: 2353 };
      const students: IStudent[] = [{ id: 9978 }];
      parent.students = students;

      const studentCollection: IStudent[] = [{ id: 9978 }];
      jest.spyOn(studentService, 'query').mockReturnValue(of(new HttpResponse({ body: studentCollection })));
      const additionalStudents = [...students];
      const expectedCollection: IStudent[] = [...additionalStudents, ...studentCollection];
      jest.spyOn(studentService, 'addStudentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ parent });
      comp.ngOnInit();

      expect(studentService.query).toHaveBeenCalled();
      expect(studentService.addStudentToCollectionIfMissing).toHaveBeenCalledWith(
        studentCollection,
        ...additionalStudents.map(expect.objectContaining),
      );
      expect(comp.studentsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const parent: IParent = { id: 2353 };
      const students: IStudent = { id: 9978 };
      parent.students = [students];

      activatedRoute.data = of({ parent });
      comp.ngOnInit();

      expect(comp.studentsSharedCollection).toContainEqual(students);
      expect(comp.parent).toEqual(parent);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParent>>();
      const parent = { id: 10134 };
      jest.spyOn(parentFormService, 'getParent').mockReturnValue(parent);
      jest.spyOn(parentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: parent }));
      saveSubject.complete();

      // THEN
      expect(parentFormService.getParent).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(parentService.update).toHaveBeenCalledWith(expect.objectContaining(parent));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParent>>();
      const parent = { id: 10134 };
      jest.spyOn(parentFormService, 'getParent').mockReturnValue({ id: null });
      jest.spyOn(parentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parent: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: parent }));
      saveSubject.complete();

      // THEN
      expect(parentFormService.getParent).toHaveBeenCalled();
      expect(parentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParent>>();
      const parent = { id: 10134 };
      jest.spyOn(parentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(parentService.update).toHaveBeenCalled();
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
  });
});
