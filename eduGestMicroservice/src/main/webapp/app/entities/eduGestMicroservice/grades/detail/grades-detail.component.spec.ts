import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { GradesDetailComponent } from './grades-detail.component';

describe('Grades Management Detail Component', () => {
  let comp: GradesDetailComponent;
  let fixture: ComponentFixture<GradesDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GradesDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./grades-detail.component').then(m => m.GradesDetailComponent),
              resolve: { grades: () => of({ id: 25513 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(GradesDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GradesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load grades on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', GradesDetailComponent);

      // THEN
      expect(instance.grades()).toEqual(expect.objectContaining({ id: 25513 }));
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
