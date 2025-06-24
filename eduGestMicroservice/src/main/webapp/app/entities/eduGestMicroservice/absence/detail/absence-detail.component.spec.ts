import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { AbsenceDetailComponent } from './absence-detail.component';

describe('Absence Management Detail Component', () => {
  let comp: AbsenceDetailComponent;
  let fixture: ComponentFixture<AbsenceDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AbsenceDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./absence-detail.component').then(m => m.AbsenceDetailComponent),
              resolve: { absence: () => of({ id: 3675 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(AbsenceDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AbsenceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load absence on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AbsenceDetailComponent);

      // THEN
      expect(instance.absence()).toEqual(expect.objectContaining({ id: 3675 }));
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
