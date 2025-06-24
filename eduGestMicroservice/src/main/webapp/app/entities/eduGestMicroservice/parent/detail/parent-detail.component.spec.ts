import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ParentDetailComponent } from './parent-detail.component';

describe('Parent Management Detail Component', () => {
  let comp: ParentDetailComponent;
  let fixture: ComponentFixture<ParentDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParentDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./parent-detail.component').then(m => m.ParentDetailComponent),
              resolve: { parent: () => of({ id: 10134 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ParentDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ParentDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load parent on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ParentDetailComponent);

      // THEN
      expect(instance.parent()).toEqual(expect.objectContaining({ id: 10134 }));
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
