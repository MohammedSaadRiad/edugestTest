import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { SchoolClassDetailComponent } from './school-class-detail.component';

describe('SchoolClass Management Detail Component', () => {
  let comp: SchoolClassDetailComponent;
  let fixture: ComponentFixture<SchoolClassDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SchoolClassDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./school-class-detail.component').then(m => m.SchoolClassDetailComponent),
              resolve: { schoolClass: () => of({ id: 21619 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SchoolClassDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SchoolClassDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load schoolClass on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SchoolClassDetailComponent);

      // THEN
      expect(instance.schoolClass()).toEqual(expect.objectContaining({ id: 21619 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
