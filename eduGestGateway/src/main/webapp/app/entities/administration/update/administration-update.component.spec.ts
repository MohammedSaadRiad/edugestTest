import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { AdministrationService } from '../service/administration.service';
import { IAdministration } from '../administration.model';
import { AdministrationFormService } from './administration-form.service';

import { AdministrationUpdateComponent } from './administration-update.component';

describe('Administration Management Update Component', () => {
  let comp: AdministrationUpdateComponent;
  let fixture: ComponentFixture<AdministrationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let administrationFormService: AdministrationFormService;
  let administrationService: AdministrationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AdministrationUpdateComponent],
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
      .overrideTemplate(AdministrationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AdministrationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    administrationFormService = TestBed.inject(AdministrationFormService);
    administrationService = TestBed.inject(AdministrationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const administration: IAdministration = { id: 17862 };

      activatedRoute.data = of({ administration });
      comp.ngOnInit();

      expect(comp.administration).toEqual(administration);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAdministration>>();
      const administration = { id: 12826 };
      jest.spyOn(administrationFormService, 'getAdministration').mockReturnValue(administration);
      jest.spyOn(administrationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ administration });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: administration }));
      saveSubject.complete();

      // THEN
      expect(administrationFormService.getAdministration).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(administrationService.update).toHaveBeenCalledWith(expect.objectContaining(administration));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAdministration>>();
      const administration = { id: 12826 };
      jest.spyOn(administrationFormService, 'getAdministration').mockReturnValue({ id: null });
      jest.spyOn(administrationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ administration: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: administration }));
      saveSubject.complete();

      // THEN
      expect(administrationFormService.getAdministration).toHaveBeenCalled();
      expect(administrationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAdministration>>();
      const administration = { id: 12826 };
      jest.spyOn(administrationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ administration });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(administrationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
